package com.mageddo.dnsproxyserver.solver.remote.application.mapper;

import com.mageddo.dnsproxyserver.solver.Response;
import org.junit.jupiter.api.Test;
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
    final var res = ResultMapper.transformToResult(failedFuture, randomReq);

    // assert
    assertNotNull(res);
    assertTrue(res.isEmpty());
  }
}
