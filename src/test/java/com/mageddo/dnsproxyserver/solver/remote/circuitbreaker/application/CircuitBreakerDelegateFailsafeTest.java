package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import testing.templates.solver.remote.FailSafeCircuitBreakerTemplates;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CircuitBreakerDelegateFailsafeTest {

  @Test
  void mustThrowAbstractOpenCircuitException() {
    // arrange
    final var circuitBreaker = new CircuitBreakerDelegateFailsafe(FailSafeCircuitBreakerTemplates.fastCircuit());

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
}
