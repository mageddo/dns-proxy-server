package com.mageddo.dnsproxyserver.server.dns.solver;

import testing.templates.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SolverCachedRemoteTest {

  @Mock
  SolverCache solverCache;

  @Spy
  @InjectMocks
  SolverCachedRemote solver;

  @Test
  void mustLeadWithNullResponses(){
    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNull(res);
  }
}
