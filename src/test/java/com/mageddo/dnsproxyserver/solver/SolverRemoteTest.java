package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.application.CircuitBreakerNonResilientService;
import com.mageddo.utils.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;
import testing.templates.InetSocketAddressTemplates;
import testing.templates.MessageTemplates;
import testing.templates.solver.remote.ResultTemplates;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolverRemoteTest {

  @Mock
  Resolver resolver;

  @Mock
  Resolver resolver2;

  @Mock
  RemoteResolvers resolvers;

  @Spy
  CircuitBreakerNonResilientService circuitBreakerService;

  @Spy
  @InjectMocks
  SolverRemote solverRemote;

  @BeforeEach
  void beforeEach (){
    lenient()
      .doReturn(Executors.newThreadExecutor())
      .when(this.resolvers)
      .getExecutor();
  }

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
      .sendAsync(any(), any(Executor.class));


    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertEquals(Response.DEFAULT_SUCCESS_TTL, res.getDpsTtl());
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
      .sendAsync(any(), any(Executor.class));


    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertEquals(Response.DEFAULT_NXDOMAIN_TTL, res.getDpsTtl());
  }

  @Test
  void mustReturnNullWhenGetTimeout() {

    // arrange
    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.failedFuture(new SocketTimeoutException("Deu ruim")))
      .when(this.resolver)
      .sendAsync(any(), any(Executor.class));

    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = this.solverRemote.handle(query);

    // assert
    assertNull(res);
  }

  @Test
  void mustReturnRaEvenWhenRemoteServerDoesntReturnsRA() throws Exception {
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
      .sendAsync(any(), any(Executor.class));

    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

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

    doReturn(InetSocketAddressTemplates._8_8_8_8())
      .when(this.resolver)
      .getAddress()
    ;

    doReturn(CompletableFuture.completedFuture(answer))
      .when(this.resolver)
      .sendAsync(any(), any(Executor.class));

    doReturn(List.of(this.resolver))
      .when(this.resolvers)
      .resolvers()
    ;

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

  @Test
  void mustNotUseResolversWithOpenCircuit(){
    // arrange

    final var server1 = new SimpleResolver(InetSocketAddressTemplates._8_8_8_8());
    final var server2 = new SimpleResolver(InetSocketAddressTemplates._8_8_4_4());
    final var server3 = new SimpleResolver(InetSocketAddressTemplates._1_1_1_1());

    final var query = MessageTemplates.acmeAQuery();
    final var result = ResultTemplates.error();

    doReturn(CircuitStatus.OPEN)
      .when(this.circuitBreakerService)
      .getCircuitStatus(server1.getAddress());

    doReturn(CircuitStatus.CLOSED)
      .when(this.circuitBreakerService)
      .getCircuitStatus(server2.getAddress());

    doReturn(result)
      .when(this.solverRemote)
      .safeQueryResult(any());

    doReturn(List.of(server1, server2, server3))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    this.solverRemote.handle(query);

    final var resolversToUse = findResolversToUse()
      ;
    // assert
    verify(this.solverRemote, times(2)).safeQueryResult(any());
    assertEquals("[OPEN, CLOSED, null]", String.valueOf(this.solverRemote.getResolversStats()));
    assertEquals("[/8.8.4.4:53, /1.1.1.1:53]", resolversToUse);
  }


  @Test
  void mustFindOnlyNotOpenedCircuits(){
    // arrange

    final var server1 = new SimpleResolver(InetSocketAddressTemplates._8_8_8_8());
    final var server2 = new SimpleResolver(InetSocketAddressTemplates._8_8_4_4());
    final var server3 = new SimpleResolver(InetSocketAddressTemplates._1_1_1_1());

    final var query = MessageTemplates.acmeAQuery();
    final var result = ResultTemplates.error();

    doReturn(CircuitStatus.OPEN)
      .when(this.circuitBreakerService)
      .getCircuitStatus(server1.getAddress());

    doReturn(CircuitStatus.CLOSED)
      .when(this.circuitBreakerService)
      .getCircuitStatus(server2.getAddress());

    doReturn(result)
      .when(this.solverRemote)
      .safeQueryResult(any());

    doReturn(List.of(server1, server2, server3))
      .when(this.resolvers)
      .resolvers()
    ;

    // act
    this.solverRemote.handle(query);

    final var resolversToUse = findResolversToUse()
      ;
    // assert
    verify(this.solverRemote, times(2)).safeQueryResult(any());
    assertEquals("[OPEN, CLOSED, null]", String.valueOf(this.solverRemote.getResolversStats()));
    assertEquals("[/8.8.4.4:53, /1.1.1.1:53]", resolversToUse);
  }

  private String findResolversToUse() {
    return this.solverRemote.findResolversWithNonOpenCircuit()
      .stream()
      .map(Resolver::getAddress)
      .toList()
      .toString();
  }

}
