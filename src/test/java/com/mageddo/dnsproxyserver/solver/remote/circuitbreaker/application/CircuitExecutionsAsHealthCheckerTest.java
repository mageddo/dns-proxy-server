package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.solver.remote.ResultSupplierTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CircuitExecutionsAsHealthCheckerTest {

  @Mock
  CircuitBreakerDelegate circuitBreaker;

  @InjectMocks
  CircuitExecutionsAsHealthChecker obj;

  @Test
  void mustUseTheLastCallAsHealthCheck() {

    final var sup = ResultSupplierTemplates.withCallsCounterNullRes();

    this.obj.execute(sup);
    this.obj.isHealthy();

    assertEquals(1, sup.getCalls());
  }
}
