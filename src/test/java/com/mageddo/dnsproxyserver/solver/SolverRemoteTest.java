package com.mageddo.dnsproxyserver.solver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;
import testing.templates.MessageTemplates;
import testing.templates.solver.remote.ResolverTemplates;

import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolverRemoteTest {

  @Spy
  @InjectMocks
  SolverRemote solverRemote;

  @Test
  void mustCacheSolvedQueryFor5Minutes() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildAAnswer(query);

    doReturn(ResolverTemplates.googleDnsAsList())
      .when(this.solverRemote)
      .findResolversToUse()
    ;

    doReturn(CompletableFuture.completedFuture(answer))
      .when(this.solverRemote)
      .sendQueryAsyncToResolver(any());

    this.excludeCircuitBreakerStrategyAndCallQueryMethodDirectly();

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertEquals(Response.DEFAULT_SUCCESS_TTL, res.getDpsTtl());
  }

  @Test
  void mustCacheNxDomainQueryFor1Hour() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildNXAnswer(query);

    doReturn(ResolverTemplates.googleDnsAsList())
        .when(this.solverRemote)
        .findResolversToUse()
    ;

    doReturn(CompletableFuture.completedFuture(answer))
        .when(this.solverRemote)
        .sendQueryAsyncToResolver(any());

    this.excludeCircuitBreakerStrategyAndCallQueryMethodDirectly();

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertEquals(Response.DEFAULT_NXDOMAIN_TTL, res.getDpsTtl());
  }

  @Test
  void mustReturnNullWhenGetTimeout() {

    // arrange
    doReturn(ResolverTemplates.googleDnsAsList())
        .when(this.solverRemote)
        .findResolversToUse()
    ;

    doReturn(CompletableFuture.failedFuture(new SocketTimeoutException("Deu ruim")))
        .when(this.solverRemote)
        .sendQueryAsyncToResolver(any());

    this.excludeCircuitBreakerStrategyAndCallQueryMethodDirectly();

    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertNull(res);
  }

  @Test
  void mustReturnRaEvenWhenRemoteServerDoesntReturnsRA() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var res = MessageTemplates.buildAAnswer(query);
    res.getHeader().unsetFlag(Flags.RA);

    doReturn(ResolverTemplates.googleDnsAsList())
        .when(this.solverRemote)
        .findResolversToUse()
    ;

    doReturn(CompletableFuture.completedFuture(res))
        .when(this.solverRemote)
        .sendQueryAsyncToResolver(any());

    this.excludeCircuitBreakerStrategyAndCallQueryMethodDirectly();

    // act
    final var result = this.solverRemote.handle(query);

    // assert
    assertTrue(Responses.hasFlag(result, Flags.RA));
    assertEquals(Response.DEFAULT_SUCCESS_TTL, result.getDpsTtl());
  }

  @Test
  void mustPingRemoteServerWhileQueryingWhenFeatureIsActive(){

    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildAAnswer(query);

    doReturn(true).when(this.solverRemote).isPingWhileGettingQueryResponseActive();

    doReturn(ResolverTemplates.googleDnsAsList())
        .when(this.solverRemote)
        .findResolversToUse()
    ;

    doReturn(CompletableFuture.completedFuture(answer))
        .when(this.solverRemote)
        .sendQueryAsyncToResolver(any());

    this.excludeCircuitBreakerStrategyAndCallQueryMethodDirectly();

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertNotNull(res);
    verify(this.solverRemote).pingWhileGettingQueryResponse(any(), any());

  }

  @Test
  void pingRemoteServerWhileQueryingDisabledByDefault(){

    // act
    final var active = this.solverRemote.isPingWhileGettingQueryResponseActive();

    // assert
    assertFalse(active);

  }

  void excludeCircuitBreakerStrategyAndCallQueryMethodDirectly() {
    doAnswer(iom -> Supplier.class.cast(iom.getArgument(1)).get())
      .when(this.solverRemote)
      .queryUsingCircuitBreaker(any(), any())
    ;
  }
}
