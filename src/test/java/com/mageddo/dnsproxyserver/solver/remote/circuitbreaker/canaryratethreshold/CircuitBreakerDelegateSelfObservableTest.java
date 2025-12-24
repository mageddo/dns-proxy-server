package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import java.time.Duration;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthChecker;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor.StateTransitor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
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
    lenient().doReturn(CircuitStatus.CLOSED)
        .when(this.delegate)
        .findStatus()
    ;
    this.strategy = spy(new CircuitBreakerDelegateSelfObservable(
        this.delegate,
        Duration.ofMillis(1000 / 300),
        this.healthChecker
    ));
  }

  @AfterEach
  void afterEach() {
    this.strategy.close();
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
    final var stateTransitor = mock(StateTransitor.class);
    doReturn(stateTransitor)
        .when(this.delegate)
        .stateTransitor();

    doReturn(CircuitStatus.OPEN)
        .when(this.delegate)
        .findStatus()
    ;
    doReturn(true)
        .when(this.healthChecker)
        .isHealthy()
    ;

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

