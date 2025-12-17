package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.dns.utils.Messages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Rcode;

import testing.templates.MessageTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SolverStubTest {

  @Spy
  SolverStub solver;

  @BeforeEach
  void beforeEach() {
    lenient()
        .doReturn("stub")
        .when(this.solver)
        .findDomainName();
  }

  @Test
  void mustValidateNonSupportedQuestionType() {
    final var query = MessageTemplates.acmeSoaQuery();

    final var response = this.solver.handle(query);

    assertNull(response);
  }

  @Test
  void mustValidateIncompatibleDomainName() {
    final var query = MessageTemplates.acmeAQuery();

    final var response = this.solver.handle(query);

    assertNull(response);
  }

  @Test
  void mustFindRightIpAddress() {
    final var query = MessageTemplates.dpsStubAQuery();

    final var response = this.solver.handle(query);

    assertNotNull(response);
    assertEquals("192.168.3.1", Messages.findAnswerRawIP(response.getMessage()));
  }

  @Test
  void willIgnoreHostnameWithRightDomainButNotEmbeddedIp() {
    final var query = MessageTemplates.stubAQueryWithoutIp();

    final var response = this.solver.handle(query);

    assertNull(response);
  }

  @Test
  void mustAnswerNxWhenQueryTypeIsNotEqualsToIpVersion() {
    final var query = MessageTemplates.stubAQueryWithIpv6AnswerIp();

    final var response = this.solver.handle(query);

    assertNotNull(response);
    assertEquals(Rcode.NXDOMAIN, response.getRCode());
  }
}
