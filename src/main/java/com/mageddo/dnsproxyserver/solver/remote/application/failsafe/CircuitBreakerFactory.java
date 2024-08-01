package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;
import dev.failsafe.event.CircuitBreakerStateChangedEvent;
import dev.failsafe.event.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFactory {

  private final Map<InetSocketAddress, CircuitBreaker<Result>> circuitBreakerMap = new ConcurrentHashMap<>();
  private final ConfigService configService;
  private final CircuitBreakerPingCheckerService circuitBreakerCheckerService;
  private final SolverConsistencyGuaranteeDAO solverConsistencyGuaranteeDAO;

  public Result check(InetSocketAddress remoteAddress, Supplier<Result> sup) {
    final var circuitBreaker = this.createOrGetCircuitBreaker(remoteAddress);
    return Failsafe
      .with(circuitBreaker)
      .get((ctx) -> sup.get());
  }

  CircuitBreaker<Result> createOrGetCircuitBreaker(InetSocketAddress address) {
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
      .onClose(build(CircuitStatus.CLOSED, address))
      .onOpen(build(CircuitStatus.OPEN, address))
      .build();
  }

  EventListener<CircuitBreakerStateChangedEvent> build(CircuitStatus actualStateName, InetSocketAddress address) {
    return event -> {
      final var previousStateName = CircuitBreakerStateMapper.toStateNameFrom(event);
      if (isHalfOpenToOpen(previousStateName, actualStateName)) {
        log.trace("status=ignoredTransition, from={}, to={}", previousStateName, actualStateName);
        return;
      }
      log.trace(
        "status=beforeFlushCaches, address={}, previous={}, actual={}", address, previousStateName, actualStateName
      );
      this.flushCache();
      log.debug(
        "status=clearedCache, address={}, previous={}, actual={}", address, previousStateName, actualStateName
      );
    };
  }

  private static boolean isHalfOpenToOpen(CircuitStatus previousStateName, CircuitStatus actualStateName) {
    return CircuitStatus.HALF_OPEN.equals(previousStateName) && CircuitStatus.OPEN.equals(actualStateName);
  }

  void flushCache() {
    this.solverConsistencyGuaranteeDAO.flushCachesFromCircuitBreakerStateChange();
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

  public void reset() {
    this.circuitBreakerMap.clear();
  }

  public List<Stats> stats() {
    return this.circuitBreakerMap.keySet()
      .stream()
      .map(this::toStats)
      .toList();
  }

  public CircuitStatus getStatus(InetSocketAddress remoteAddress) {
    return CircuitBreakerStateMapper.fromFailSafeCircuitBreaker(this.circuitBreakerMap.get(remoteAddress));
  }

  private Stats toStats(InetSocketAddress remoteAddr) {
    final var circuitBreaker = this.circuitBreakerMap.get(remoteAddr);
    final var state = circuitBreaker.getState().name();
    return Stats.of(remoteAddr.toString(), state);
  }


  @Value
  public static class Stats {

    private String remoteServerAddress;
    private String state;

    public static Stats of(String remoteServerAddress, String state) {
      return new Stats(remoteServerAddress, state);
    }
  }

}
