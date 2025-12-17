package com.mageddo.circuitbreaker.failsafe;

import com.mageddo.commons.concurrent.Threads;

import org.junit.jupiter.api.Test;

import testing.templates.solver.remote.FailSafeCircuitBreakerTemplates;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircuitStatusRefreshTest {

  @Test
  void mustChangeOpenStateToHalfOpenStateWhenTimeHasIsExpired() {

    // arrange
    final var circuitBreaker = FailSafeCircuitBreakerTemplates.fastOpenCircuit();

    // act
    Threads.sleep(100);
    final var refreshed = CircuitStatusRefresh.refresh(circuitBreaker);

    // assert
    assertTrue(refreshed);
    assertTrue(circuitBreaker.isHalfOpen());

  }

  @Test
  void mustKeepStateAsOpenWhenTimeHasNotExpired() {

    // arrange
    final var circuitBreaker = FailSafeCircuitBreakerTemplates.fastOpenCircuit();

    // act
    final var refreshed = CircuitStatusRefresh.refresh(circuitBreaker);

    // assert
    assertFalse(refreshed);
    assertTrue(circuitBreaker.isOpen());

  }

  @Test
  void closedStateWillNotBeAffected() {

    // arrange
    final var circuitBreaker = FailSafeCircuitBreakerTemplates.fastCircuit();

    // act
    Threads.sleep(100);
    final var refreshed = CircuitStatusRefresh.refresh(circuitBreaker);

    // assert
    assertFalse(refreshed);
    assertTrue(circuitBreaker.isClosed());

  }

  @Test
  void mustIgnoreNulls() {
    final var refreshed = CircuitStatusRefresh.refresh(null);
    assertFalse(refreshed);
  }
}
