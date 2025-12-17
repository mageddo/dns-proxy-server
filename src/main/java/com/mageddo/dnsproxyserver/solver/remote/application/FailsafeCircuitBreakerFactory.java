package com.mageddo.dnsproxyserver.solver.remote.application;

import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;

import dev.failsafe.CircuitBreaker;
import dev.failsafe.event.CircuitBreakerStateChangedEvent;
import dev.failsafe.event.EventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class FailsafeCircuitBreakerFactory {

  private final OnCacheMustBeFlushedEvent onCacheMustBeFlushedEvent;

  public CircuitBreaker<Result> build(
      InetSocketAddress address, StaticThresholdCircuitBreakerStrategyConfig config
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

  private EventListener<CircuitBreakerStateChangedEvent> build(
      CircuitStatus actualStateName, InetSocketAddress address
  ) {
    return event -> {
      final var previousStateName = CircuitBreakerStateMapper.toStateNameFrom(event);
      if (isHalfOpenToOpen(previousStateName, actualStateName)) {
        log.trace("status=ignoredTransition, from={}, to={}", previousStateName, actualStateName);
        return;
      }
      log.trace(
          "status=beforeFlushCaches, address={}, previous={}, actual={}", address,
          previousStateName, actualStateName
      );
      this.onCacheMustBeFlushedEvent.run();
      log.debug(
          "status=clearedCache, address={}, previous={}, actual={}", address, previousStateName,
          actualStateName
      );
    };
  }


  private static boolean isHalfOpenToOpen(CircuitStatus previousStateName,
      CircuitStatus actualStateName) {
    return CircuitStatus.HALF_OPEN.equals(previousStateName) && CircuitStatus.OPEN.equals(
        actualStateName);
  }
}
