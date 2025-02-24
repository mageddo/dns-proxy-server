package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthChecker;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor.StateTransitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
    this.strategy = spy(new CircuitBreakerDelegateSelfObservable(
      this.delegate,
      Duration.ofMillis(1000 / 30),
      this.healthChecker
    ));
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

    final var stateTransitor = mock(StateTransitor.class);
    doReturn(stateTransitor)
      .when(this.delegate)
      .stateTransitor();

    // act
    Threads.sleep(1000);

    // assert
    verify(stateTransitor, atLeastOnce()).halfOpen();

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
