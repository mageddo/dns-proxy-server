package com.mageddo.dnsproxyserver.solver.remote.application.mapper;

import com.mageddo.dnsproxyserver.solver.Response;
import org.junit.jupiter.api.Test;
import testing.templates.MessageTemplates;
import testing.templates.solver.remote.RequestTemplates;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
