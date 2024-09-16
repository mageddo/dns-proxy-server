package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerDelegateSelfObservableTest {

  CircuitBreakerDelegateSelfObservable strategy;

  @Mock
  CircuitBreakerDelegateCanaryRateThreshold delegate;

  @Mock
  HealthChecker healthChecker;

  @BeforeEach
  void beforeEach() {
    this.strategy = new CircuitBreakerDelegateSelfObservable(
      this.delegate,
      Duration.ofMillis(1000 / 30),
      this.healthChecker
    );
  }

  @Test
  void mustStartBackgroundTaskWhenCreatingObject() {

    // act
    Threads.sleep(300);

    // assert
    verify(this.delegate, atLeastOnce()).findStatus();

  }

  @Test
  void mustHalfOpenCircuitAfterConfiguredTimeAndSatisfyHealthCheck() {

    // arrange
    doReturn(CircuitStatus.OPEN)
      .when(this.delegate)
      .findStatus()
    ;
    doReturn(true)
      .when(this.healthChecker)
      .isHealthy()
    ;

    // act
    Threads.sleep(300);

    // assert
    verify(this.delegate, atLeastOnce()).transitionToHalfOpenState();

  }

  @Test
  void mustNotHalfOpenCircuitAfterHealthCheckRunAndGetNoSuccess() {
    // arrange
    doReturn(CircuitStatus.OPEN)
      .when(this.delegate)
      .findStatus()
    ;

    // act
    Threads.sleep(300);

    // assert
    assertEquals(CircuitStatus.OPEN, this.strategy.findStatus());
  }
}
