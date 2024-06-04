package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import dagger.sheath.InjectMock;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;
import testing.templates.solver.remote.RequestTemplates;

import javax.inject.Inject;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class CircuitBreakerFailSafeServiceCompTest {

  @Inject
  CircuitBreakerFailSafeService service;

  @InjectMock
  SolverConsistencyGuaranteeDAO consistencyGuaranteeDAO;

  @BeforeEach
  void beforeEach() {
    this.service.resetCircuitBreakerFactory();
  }

  @Test
  void mustOpenCircuitAfterThresholdFailures() {
    // arrange
    final var req = RequestTemplates.buildDefault();
    final Supplier<Result> failureSup = () -> {
      throw new CircuitCheckException("mocked failure");
    };

    // act
    this.trySafeHandleReqThreeTimes(req, failureSup);

    // assert
    final var result = this.service.safeHandle(req.getResolverAddress(), failureSup);
    assertTrue(result.isEmpty());
    assertEquals("CircuitBreakerOpenException for /8.8.8.8:53", this.service.getStatus());

  }

  @Test
  void mustFlushCachesWhenCircuitBreakerStateChanges() {
    // arrange // act
    this.mustOpenCircuitAfterThresholdFailures();

    // assert
    verify(this.consistencyGuaranteeDAO).flushCachesFromCircuitBreakerStateChange();
  }

  void trySafeHandleReqThreeTimes(Request req, Supplier<Result> failureSup) {
    for (int i = 0; i < 3; i++) {
      final var result = this.service.safeHandle(req.getResolverAddress(), failureSup);
      assertTrue(result.isEmpty());
      assertEquals("CircuitCheckException for /8.8.8.8:53", this.service.getStatus());
    }
  }

}
