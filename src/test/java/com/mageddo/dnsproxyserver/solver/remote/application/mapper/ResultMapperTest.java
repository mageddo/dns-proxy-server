package com.mageddo.dnsproxyserver.solver.remote.application.mapper;

import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Responses;
import org.junit.jupiter.api.Test;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import testing.templates.MessageTemplates;
import testing.templates.solver.remote.RequestTemplates;

import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultMapperTest {

  @Test
  void mustCacheSolvedQueryFor5Minutes() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildAAnswer(query);

    final var resFuture = CompletableFuture.completedFuture(answer);
    final var randomReq = RequestTemplates.buildDefault();

    // act
    final var result = ResultMapper.from(resFuture, randomReq);

    // assert
    final var successResponse = result.getSuccessResponse();
    assertNotNull(successResponse);
    assertEquals(Response.DEFAULT_SUCCESS_TTL, successResponse.getDpsTtl());
  }

  @Test
  void mustCacheNxDomainQueryFor1Hour() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var answer = MessageTemplates.buildNXAnswer(query);

    final var resFuture = CompletableFuture.completedFuture(answer);
    final var randomReq = RequestTemplates.buildDefault();

    // act
    final var result = ResultMapper.from(resFuture, randomReq);

    // assert
    final var errorResponse = result.getErrorResponse();
    assertNotNull(errorResponse);
    assertEquals(Response.DEFAULT_NXDOMAIN_TTL, errorResponse.getDpsTtl());

  }

  @Test
  void mustReturnNullWhenGetTimeout() {

    // arrange
    final CompletableFuture<Message> failedFuture = CompletableFuture.failedFuture(new SocketTimeoutException("Deu ruim"));
    final var randomReq = RequestTemplates.buildDefault();

    // act
    final var res = ResultMapper.from(failedFuture, randomReq);

    // assert
    assertNotNull(res);
    assertTrue(res.isEmpty());
  }

  @Test
  void mustReturnRaEvenWhenRemoteServerDoesntReturnsRA() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var res = MessageTemplates.buildAAnswerWithoutRA(query);
    final var future = CompletableFuture.completedFuture(res);

    // act
    final var result = ResultMapper.from(future, RequestTemplates.buildDefault())
      .getSuccessResponse();

    // assert
    assertTrue(Responses.hasFlag(result, Flags.RA));
    assertEquals(Response.DEFAULT_SUCCESS_TTL, result.getDpsTtl());
  }

}
