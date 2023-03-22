package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Resolver;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class SolverRemoteTest {

  @Mock
  Resolver resolver;

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

    doReturn(answer)
      .when(this.resolver)
      .send(any())
    ;

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

    doReturn(answer)
      .when(this.resolver)
      .send(any())
    ;

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

    doThrow(new IOException("Deu ruim"))
      .when(this.resolver)
      .send(any())
    ;

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

    doReturn(res)
      .when(this.resolver)
      .send(any())
    ;

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

}
