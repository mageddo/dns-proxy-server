package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.solver.remote.ResultSupplierTemplates;

import javax.inject.Inject;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class CircuitBreakerFactoryCompTest {

  @Inject
  CircuitBreakerFactory factory;

  @Test
  void mustExecuteHappyPath() {

    final var sup = ResultSupplierTemplates.withCallsCounterNullRes();
    final var config = CircuitBreakerConfigTemplates.fastCanaryRateThreshold();

    final var circuitBreaker = this.factory.build(config);
    final var result = circuitBreaker.execute(sup);

    assertEquals(1, sup.getCalls());
    assertNull(result);

  }

  @Test
  void mustTestCircuitWhenItIsOpen() {

    final var sup = ResultSupplierTemplates.withCallsCounterNullRes();
    final var config = CircuitBreakerConfigTemplates.fastCanaryRateThreshold();

    final var circuitBreaker = this.factory.build(config);
    assertEquals(CircuitStatus.CLOSED, circuitBreaker.findStatus());

    assertThrows(CircuitCheckException.class, () -> circuitBreaker.execute(ResultSupplierTemplates.alwaysFail()));
    assertEquals(CircuitStatus.OPEN, circuitBreaker.findStatus());

    assertThrows(CircuitIsOpenException.class, () -> circuitBreaker.execute(sup));

    Threads.sleep(Duration.ofMillis(1200));

    assertEquals(CircuitStatus.HALF_OPEN, circuitBreaker.findStatus());
    assertEquals(1, sup.getCalls());

  }
}
