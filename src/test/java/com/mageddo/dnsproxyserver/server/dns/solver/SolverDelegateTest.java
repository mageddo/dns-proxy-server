package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.templates.EntryTemplates;
import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import com.mageddo.dnsproxyserver.templates.SolverMockTemplates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.mageddo.utils.Assertions.validResponse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SolverDelegateTest {

  @Mock
  SolverProvider solverProvider;

  SolverDelegate delegate;

  @BeforeEach
  void beforeEach() {
    this.delegate = new SolverDelegate(this.solverProvider);
  }

  @Test
  void mustDelegateAndGetValidAnswer() {

    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var cname = EntryTemplates.cname("acme.com", "acme.com.br");

    doReturn(List.of(SolverMockTemplates.whateverMock("acme.com.br")))
      .when(this.solverProvider)
      .getSolversExcludingLocalDB();

    // act
    final var response = this.delegate.solve(query, cname);

    // assert
    assertNotNull(response);
    validResponse(response.getMessage());

  }
}
