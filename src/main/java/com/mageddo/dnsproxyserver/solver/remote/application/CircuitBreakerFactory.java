package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.event.CircuitBreakerStateChangedEvent;
import dev.failsafe.event.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFactory {

  private final Map<InetSocketAddress, CircuitBreaker<Result>> circuitBreakerMap = new ConcurrentHashMap<>();
  private final ConfigService configService;
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
//      .onClose(bindStateChangeEvent("closed", address))
//      .onOpen(bindStateChangeEvent("open", address))
//      .onHalfOpen(bindStateChangeEvent("half-open", address))
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
}
