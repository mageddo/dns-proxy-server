package com.mageddo.dnsproxyserver.server.dns.solver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Rcode;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.MessageTemplates;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SolverRemoteTest {

  @Mock
  Resolver resolver;

  @Mock
  Resolver resolver2;

  @Mock
  RemoteResolvers resolvers;

  @Spy
  @InjectMocks
  SolverRemote solverRemote;

  @Test
  void mustCacheSolvedQueryFor5Minutes() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildAAnswer(query);

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.completedFuture(answer))
      .when(this.resolver)
      .sendAsync(any());


    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertEquals(SolverRemote.DEFAULT_SUCCESS_TTL, res.getTtl());
  }

  @Test
  void mustCacheNxDomainQueryFor1Hour() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildNXAnswer(query);

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.completedFuture(answer))
      .when(this.resolver)
      .sendAsync(any());


    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertEquals(SolverRemote.DEFAULT_NXDOMAIN_TTL, res.getTtl());
  }

  @Test
  void mustReturnNullWhenGetTimeout() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.failedFuture(new SocketTimeoutException("Deu ruim")))
      .when(this.resolver)
        .sendAsync(any());

    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertNull(res);
  }

  @Test
  void mustReturnRaEvenWhenRemoteServerDoesntReturns() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var res = MessageTemplates.buildAAnswer(query);
    res.getHeader().unsetFlag(Flags.RA);

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.completedFuture(res))
      .when(this.resolver)
      .sendAsync(any());

    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    final var result = this.solverRemote.handle(query);

    // assert
    assertTrue(Responses.hasFlag(result, Flags.RA));
    assertEquals(SolverRemote.DEFAULT_SUCCESS_TTL, result.getTtl());
  }


  @Test
  void mustOpenCircuitAfterThresholdFailures() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.failedFuture(new SocketTimeoutException(SolverRemote.QUERY_TIMED_OUT_MSG)))
      .when(this.resolver)
      .sendAsync(any());

    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    // assert
    assertNull(this.solverRemote.handle(query));
    assertEquals("CircuitCheckException for /8.8.8.8:53", this.solverRemote.getStatus());

    assertNull(this.solverRemote.handle(query));
    assertEquals("CircuitCheckException for /8.8.8.8:53", this.solverRemote.getStatus());

    assertNull(this.solverRemote.handle(query));
    assertEquals("CircuitCheckException for /8.8.8.8:53", this.solverRemote.getStatus());

    assertNull(this.solverRemote.handle(query));
    assertEquals("CircuitBreakerOpenException for /8.8.8.8:53", this.solverRemote.getStatus());

  }


  @Test
  void mustCheckNextServerAfterCircuitFailure() throws Exception {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var res = MessageTemplates.acmeAResponse();

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver2)
      .getAddress()
    ;

    doReturn(CompletableFuture.failedFuture(new SocketTimeoutException(SolverRemote.QUERY_TIMED_OUT_MSG)))
      .when(this.resolver)
      .sendAsync(any());

    doReturn(CompletableFuture.completedFuture(res))
      .when(this.resolver2)
      .sendAsync(any());

    doReturn(List.of(this.resolver, this.resolver2))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    // assert
    final var msg = this.solverRemote.handle(query);
    assertNotNull(msg);
    assertEquals("CircuitCheckException for /8.8.8.8:53", this.solverRemote.getStatus());
    assertEquals(Rcode.NOERROR, msg.getMessage().getRcode());

  }
}
