package com.mageddo.supporting.circuitbreaker.testing.sandbox.resilience4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;

import com.mageddo.supporting.circuitbreaker.testing.sandbox.AbstractCircuitBreakerSandBox;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.Result;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.State;
import com.mageddo.supporting.circuitbreaker.testing.sandbox.Stats;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

public class Resilience4jCircuitBreakerSandBox extends AbstractCircuitBreakerSandBox {

  private final CircuitBreaker circuitBreaker;

  public Resilience4jCircuitBreakerSandBox(final CircuitBreaker circuitBreaker) {
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

  public static String runError(CircuitBreaker breaker) {
    return breaker.executeSupplier(() -> {
      throw new UncheckedIOException(new IOException("an error"));
    });
  }

  public static String runSuccess(CircuitBreaker breaker) {
    return breaker.executeSupplier(() -> LocalDateTime.now()
        .toString());
  }

  public static void testCircuitOnError(
      final Result expectedResult, final CircuitBreaker.State expectedState,
      final CircuitBreaker circuit, final int times
  ) {
    of(circuit).testCircuitOnError(expectedResult, StateMapper.from(expectedState), times);
  }

  public static void testCircuitOnSuccess(
      final Result expectedResult, final CircuitBreaker.State expectedState,
      final CircuitBreaker circuit, final int times
  ) {
    of(circuit).testCircuitOnSuccess(expectedResult, StateMapper.from(expectedState), times);
  }

  public static Resilience4jCircuitBreakerSandBox of(CircuitBreaker circuitBreaker) {
    return new Resilience4jCircuitBreakerSandBox(circuitBreaker);
  }
}
