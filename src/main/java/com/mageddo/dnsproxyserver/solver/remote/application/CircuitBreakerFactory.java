package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.event.CircuitBreakerStateChangedEvent;
import dev.failsafe.event.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFactory {

  private final Map<InetSocketAddress, CircuitBreaker<Result>> circuitBreakerMap = new ConcurrentHashMap<>();
  private final ConfigService configService;
  private final CircuitBreakerCheckerService circuitBreakerCheckerService;
  private final SolverConsistencyGuaranteeDAO solverConsistencyGuaranteeDAO;

  public CircuitBreaker<Result> createCircuitBreakerFor(InetSocketAddress address) {
    final var config = this.findCircuitBreakerConfig();
    return this.circuitBreakerMap.computeIfAbsent(address, addr -> buildCircuitBreaker(addr, config));
  }

  CircuitBreaker<Result> buildCircuitBreaker(
    InetSocketAddress address, com.mageddo.dnsproxyserver.config.CircuitBreaker config
  ) {
    return CircuitBreaker.<Result>builder()
      .handle(CircuitCheckException.class)
      .withFailureThreshold(config.getFailureThreshold(), config.getFailureThresholdCapacity())
      .withSuccessThreshold(config.getSuccessThreshold())
      .withDelay(config.getTestDelay())
      .onClose(build("CLOSED", address))
      .onOpen(build("OPEN", address))
      .onHalfOpen(build("HALF_OPEN", address))
      .build();
  }

  EventListener<CircuitBreakerStateChangedEvent> build(String actualStateName, InetSocketAddress address) {
    return event -> {
      final var previousStateName = CircuitBreakerStateMapper.toStateNameFrom(event);
      this.solverConsistencyGuaranteeDAO.flushCachesFromCircuitBreakerStateChange();
      log.debug(
        "status=clearedCache, address={}, previousStateName={}, actualStateName={}",
        address, previousStateName, actualStateName
      );
    };
  }

  com.mageddo.dnsproxyserver.config.CircuitBreaker findCircuitBreakerConfig() {
    return this.configService.findCurrentConfig()
      .getSolverRemote()
      .getCircuitBreaker();
  }

  public Pair<Integer, Integer> checkCreatedCircuits() {
    final var stopWatch = StopWatch.createStarted();
    log.debug("status=checkingCreatedCircuits, circuits={}", this.circuitBreakerMap.size());
    int successes = 0, errors = 0;
    for (final var entry : this.circuitBreakerMap.entrySet()) {
      if (this.circuitBreakerSafeCheck(entry)) {
        successes++;
      } else {
        errors++;
      }
    }
    log.debug(
      "status=checkEnded, successes={}, errors={}, circuits={}, timeElapsed={}",
      successes, errors, this.circuitBreakerMap.size(), stopWatch.getTime()
    );
    return Pair.of(successes, errors);
  }

  boolean circuitBreakerSafeCheck(Map.Entry<InetSocketAddress, CircuitBreaker<Result>> entry) {
    return this.circuitBreakerCheckerService.safeCheck(entry.getKey(), entry.getValue());
  }

  public void reset(){
    this.circuitBreakerMap.clear();
  }
}
