package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.templates.EntryTemplates;
import com.mageddo.dnsproxyserver.templates.HostnameTemplates;
import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolverLocalDBTest {

  @Spy
  @InjectMocks
  SolverLocalDB solver;

  @Test
  void mustSolveWildCardHosts() {

    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var hostname = HostnameQuery.of(HostnameTemplates.ACME_HOSTNAME);
    final var wildcardHostName = HostnameQuery.ofWildcard(hostname.getHostname());

    doReturn(EntryTemplates.acmeAAAA())
      .when(this.solver)
      .findEntryTo(eq(wildcardHostName))
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    assertEquals(
      "acme.com.    45  IN  A  10.10.0.1",
      Messages.detailedPrint(res.getMessage())
    );

    verify(this.solver, never()).findEntryTo(hostname);
    verify(this.solver).findEntryTo(wildcardHostName);

  }


}
