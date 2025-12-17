package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import java.time.Duration;

import javax.inject.Inject;

import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;

import org.junit.jupiter.api.Test;

import dagger.sheath.junit.DaggerTest;
import testing.ContextSupplier;
import testing.Events;
import testing.templates.CircuitBreakerConfigTemplates;
import testing.templates.solver.remote.ResultSupplierTemplates;

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

    final var circuitBreaker = buildTransitToClosedState(config);
    final var result = circuitBreaker.execute(sup);

    assertEquals(1, sup.getCalls());
    assertNull(result);

  }

  private CircuitBreakerDelegateSelfObservable buildTransitToClosedState(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    final var circuitBreaker = this.factory.buildWithoutHealthCheck(config);
    circuitBreaker.transitionToClosedState();
    return circuitBreaker;

  }

  @Test
  void mustTestCircuitWhenItIsOpen() {

    final var sup = ResultSupplierTemplates.withCallsCounterNullRes();
    final var config = CircuitBreakerConfigTemplates.fastCanaryRateThreshold();

    final var circuitBreaker = this.factory.buildWithoutHealthCheck(config);

    assertEquals(CircuitStatus.OPEN, circuitBreaker.findStatus());
    assertThrows(CircuitIsOpenException.class, () -> circuitBreaker.execute(sup));

    Threads.sleep(Duration.ofMillis(1200));
    assertEquals(CircuitStatus.HALF_OPEN, circuitBreaker.findStatus());

    assertNull(circuitBreaker.execute(sup));
    assertEquals(1, sup.getCalls());

  }


  @Test
  void circuitMustBeCreatedOpen() {

    final var config = CircuitBreakerConfigTemplates.fastCanaryRateThreshold();

    final var circuitBreaker = this.factory.buildWithoutHealthCheck(config);

    assertEquals(CircuitStatus.OPEN, circuitBreaker.findStatus());

  }
}
