package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import testing.templates.solver.remote.FailSafeCircuitBreakerTemplates;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CircuitBreakerDelegateStaticThresholdFailsafeTest {

  @Test
  void mustThrowAbstractOpenCircuitException() {
    // arrange
    final var circuitBreaker = buildCircuitBreaker();

    // act
    final Executable stm = () -> {
      circuitBreaker.execute(() -> {
        throw new CircuitCheckException("blaaa");
      });
    };
    assertThrows(CircuitCheckException.class, stm);
    assertThrows(CircuitIsOpenException.class, stm);

    // assert
  }

  @Test
  void mustOpenCircuitAfterThresholdFailures() {
    // arrange
    final var circuitBreaker = buildCircuitBreaker();
    final Supplier<Result> failureSup = () -> {
      throw new CircuitCheckException("mocked failure");
    };

    // act
    assertThrows(CircuitCheckException.class, () -> circuitBreaker.execute(failureSup));

    // assert
    assertThrows(CircuitIsOpenException.class, () -> circuitBreaker.execute(failureSup));

  }

  static CircuitBreakerDelegateStaticThresholdFailsafe buildCircuitBreaker() {
    return new CircuitBreakerDelegateStaticThresholdFailsafe(FailSafeCircuitBreakerTemplates.fastCircuit());
  }
}
