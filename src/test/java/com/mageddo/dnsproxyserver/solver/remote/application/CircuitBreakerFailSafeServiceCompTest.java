package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import testing.templates.solver.remote.RequestTemplates;

import javax.inject.Inject;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DaggerTest(component = Context.class)
class CircuitBreakerFailSafeServiceCompTest {

  @Inject
  CircuitBreakerFailSafeService service;

  @Test
  void mustOpenCircuitAfterThresholdFailures() throws Exception {
    // arrange
    final var req = RequestTemplates.buildDefault();
    final Supplier<Result> failureSup = () -> {
      throw new CircuitCheckException("mocked failure");
    };

    // act
    this.tryHandleReqThreeTimes(req, failureSup);

    // assert
    final var result = this.service.handle(req, failureSup);
    assertTrue(result.isEmpty());
    assertEquals("CircuitBreakerOpenException for /8.8.8.8:53", this.service.getStatus());

  }

  void tryHandleReqThreeTimes(Request req, Supplier<Result> failureSup) {
    for (int i = 0; i < 3; i++) {
      final var result = this.service.handle(req, failureSup);
      assertTrue(result.isEmpty());
      assertEquals("CircuitCheckException for /8.8.8.8:53", this.service.getStatus());
    }
  }

//
//  @Test
//  void mustCheckNextServerAfterCircuitFailure() throws Exception {
//    // arrange
//    final var query = MessageTemplates.acmeAQuery();
//    final var res = MessageTemplates.acmeAResponse();
//
//    doReturn(CircuitBreakerTemplates.buildDefault())
//      .when(this.solverRemote)
//      .findCircuitBreakerConfig();
//
//    doReturn(InetSocketAddressTemplates._8_8_8_8())
//      .when(this.resolver)
//      .getAddress()
//    ;
//
//    doReturn(InetSocketAddressTemplates._8_8_8_8())
//      .when(this.resolver2)
//      .getAddress()
//    ;
//
//    doReturn(CompletableFuture.failedFuture(new SocketTimeoutException(SolverRemote.QUERY_TIMED_OUT_MSG)))
//      .when(this.resolver)
//      .sendAsync(any());
//
//    doReturn(CompletableFuture.completedFuture(res))
//      .when(this.resolver2)
//      .sendAsync(any());
//
//    doReturn(List.of(this.resolver, this.resolver2))
//      .when(this.resolvers)
//      .resolvers()
//    ;
//
//    // act
//    // assert
//    final var msg = this.solverRemote.handle(query);
//    assertNotNull(msg);
//    assertEquals("CircuitCheckException for /8.8.8.8:53", this.solverRemote.getStatus());
//    assertEquals(Rcode.NOERROR, msg.getMessage().getRcode());
//
//  }
}
