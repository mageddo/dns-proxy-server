package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.dns.utils.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.MessageTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class SolverStubTest {

  @Spy
  SolverStub solver;

  @Test
  void mustValidateNonSupportedQuestionType(){
    final var query = MessageTemplates.acmeSoaQuery();

    final var response = this.solver.handle(query);

    assertNull(response);
  }

  @Test
  void mustValidateIncompatibleDomainName(){
    final var query = MessageTemplates.acmeAQuery();

    final var response = this.solver.handle(query);

    assertNull(response);
  }

  @Test
  void mustFindRightIpAddress(){
    final var query = MessageTemplates.dpsStubDockerAQuery();

    final var response = this.solver.handle(query);

    assertNotNull(response);
    assertEquals("192.168.3.1", Messages.findAnswerRawIP(response.getMessage()));
  }
}
