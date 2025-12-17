package com.mageddo.supporting.circuitbreaker.testing.sandbox.failsafe;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;

import com.mageddo.supporting.circuitbreaker.testing.sandbox.AbstractCircuitBreakerSandBox;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.Result;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.State;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.Stats;

import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;


public class FailSafeCircuitBreakerSandBox extends AbstractCircuitBreakerSandBox {

  private final CircuitBreaker<String> circuitBreaker;

  public FailSafeCircuitBreakerSandBox(final CircuitBreaker<String> circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  protected State getCircuitBreakerState() {
    return StateMapper.from(this.circuitBreaker.getState());
  }

  @Override
  public Result calcStats(Stats stats, Runnable r) {
    return StatsCalculator.calcStats(stats, r);
  }

  @Override
  public String runError() {
    return runError(this.circuitBreaker);
  }

  @Override
  public String runSuccess() {
    return runSuccess(this.circuitBreaker);
  }

  public static String runError(CircuitBreaker<String> breaker) {
    return Failsafe.with(breaker)
        .get(() -> {
          throw new UncheckedIOException(new IOException("an error"));
        });
  }

  public static String runSuccess(CircuitBreaker<String> breaker) {
    return Failsafe.with(breaker)
        .get(() -> LocalDateTime.now()
            .toString());
  }

  public static void testCircuitOnError(
      final Result expectedResult, final CircuitBreaker.State expectedState,
      final CircuitBreaker<String> circuit, final int times
  ) {
    of(circuit).testCircuitOnError(expectedResult, StateMapper.from(expectedState), times);
  }

  public static void testCircuitOnSuccess(
      final Result expectedResult, final CircuitBreaker.State expectedState,
      final CircuitBreaker<String> circuit, final int times
  ) {
    of(circuit).testCircuitOnSuccess(expectedResult, StateMapper.from(expectedState), times);
  }

  public static FailSafeCircuitBreakerSandBox of(CircuitBreaker<String> circuitBreaker) {
    return new FailSafeCircuitBreakerSandBox(circuitBreaker);
  }
}
