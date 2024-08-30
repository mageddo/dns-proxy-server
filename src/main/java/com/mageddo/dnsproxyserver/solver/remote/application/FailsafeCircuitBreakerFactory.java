package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategy;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.event.CircuitBreakerStateChangedEvent;
import dev.failsafe.event.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class FailsafeCircuitBreakerFactory {

  private final SolverConsistencyGuaranteeDAO solverConsistencyGuaranteeDAO;

  public CircuitBreaker<Result> build(
    InetSocketAddress address, StaticThresholdCircuitBreakerStrategy config
  ) {
    return CircuitBreaker.<Result>builder()
      .handle(CircuitCheckException.class)
      .withFailureThreshold(config.getFailureThreshold(), config.getFailureThresholdCapacity())
      .withSuccessThreshold(config.getSuccessThreshold())
      .withDelay(config.getTestDelay())
      .onClose(build(CircuitStatus.CLOSED, address))
      .onOpen(build(CircuitStatus.OPEN, address))
      .onHalfOpen(it -> log.trace("status=halfOpen, server={}", address))
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

  void flushCache() {
    this.solverConsistencyGuaranteeDAO.flushCachesFromCircuitBreakerStateChange();
  }

  private static boolean isHalfOpenToOpen(CircuitStatus previousStateName, CircuitStatus actualStateName) {
    return CircuitStatus.HALF_OPEN.equals(previousStateName) && CircuitStatus.OPEN.equals(actualStateName);
  }
}
