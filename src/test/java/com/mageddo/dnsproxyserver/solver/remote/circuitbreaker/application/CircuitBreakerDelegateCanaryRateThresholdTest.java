package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import testing.templates.circuitbreaker.Resilience4jCircuitBreakerTemplates;
import testing.templates.solver.remote.ResultTemplates;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircuitBreakerDelegateCanaryRateThresholdTest {

  CircuitBreaker circuitBreaker = Resilience4jCircuitBreakerTemplates.fastFail();

  CircuitBreakerDelegateCanaryRateThreshold delegate;

  @BeforeEach
  void beforeEach() {
    this.delegate = new CircuitBreakerDelegateCanaryRateThreshold(this.circuitBreaker, Duration.ofMillis(1000 / 30));
  }

  @Test
  void mustExecuteSupplierWithSuccess() {
    // arrange
    final var counter = new AtomicInteger();

    // act
    final var result = this.delegate.execute(() -> {
      counter.incrementAndGet();
      return ResultTemplates.success();
    });

    // assert
    assertNotNull(result);
    assertTrue(result.hasSuccessMessage());
    assertEquals(1, counter.get());
  }


  @Test
  void mustThrowSpecificExceptionWhenGetOpenCircuit() {
    // arrange
    final Supplier<Result> sup = () -> {
      throw new CircuitCheckException("Mocked Error");
    };

    // act // assert
    assertThrows(CircuitCheckException.class, () -> this.delegate.execute(sup));
    assertThrows(CircuitIsOpenException.class, () -> this.delegate.execute(sup));

  }

  @Test
  void mustProvideCircuitBreakerStatus() {

    final var status = this.delegate.findStatus();

    assertEquals(CircuitStatus.CLOSED, status);

  }


  @Test
  void mustProvideNullStatusWhenItsNotMapped() {

    this.circuitBreaker.transitionToForcedOpenState();

    final var status = this.delegate.findStatus();

    assertNull(status);

  }

  @Test
  void mustHalfOpenCircuitAfterConfiguredTimeAndSatisfyHealthCheck() {

    // arrange
    this.circuitBreaker.transitionToOpenState();

    // act
    Threads.sleep(300);

    // assert
    assertEquals(CircuitStatus.HALF_OPEN, this.delegate.findStatus());
  }

  @Disabled // FIXME #533 make test pass, jogar logica do background para o CircuitBreakerDelegateSelfObservableCanaryRateThreshold
  //                que sera um delegate para o CircuitBreakerDelegateCanaryRateThreshold
  @Test
  void mustNotHalfOpenCircuitAfterHealthCheckRunAndGetNoSuccess() {

    // arrange
    this.circuitBreaker.transitionToOpenState();

    // act
    Threads.sleep(300);

    // assert
      assertEquals(CircuitStatus.OPEN, this.delegate.findStatus());
  }
}
