package com.mageddo.dnsproxyserver.server.dns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import testing.templates.MessageTemplates;
import testing.templates.ResponseTemplates;

import static com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault.DEFAULT_GLOBAL_CACHE_DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RequestHandlerDefaultTest {

  @Spy
  @InjectMocks
  RequestHandlerDefault handler;

  @Test
  void mustCacheWithFixedTTL() {
    // arrange
    final var mesRes = MessageTemplates.acmeAResponse();

    doReturn(ResponseTemplates.acmeAResponse())
        .when(this.handler)
        .solve(eq(mesRes))
    ;

    // act
    final var res = this.handler.solveFixingCacheTTL(mesRes);

    // assert
    assertEquals(DEFAULT_GLOBAL_CACHE_DURATION, res.getDpsTtl());
  }
}
