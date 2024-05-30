package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.SolverLocalDB;
import testing.templates.EntryTemplates;
import testing.templates.HostnameTemplates;
import testing.templates.MessageTemplates;
import com.mageddo.net.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Rcode;

import java.time.Duration;

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

    doReturn(EntryTemplates.acmeA())
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
    assertEquals(Duration.ofSeconds(45), res.getDpsTtl());

    verify(this.solver, never()).findEntryTo(hostname);
    verify(this.solver).findEntryTo(wildcardHostName);

  }

  @Test
  void mustSolveQuadAQueries() {

    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();
    final var wildcardHostName = HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME, IP.Version.IPV6);

    doReturn(EntryTemplates.acmeQuadA())
      .when(this.solver)
      .findEntryTo(eq(wildcardHostName))
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    assertEquals(
      "acme.com.    45  IN  AAAA  2001:db8:1:0:0:0:0:2",
      Messages.detailedPrint(res.getMessage())
    );

    verify(this.solver).findEntryTo(wildcardHostName);

  }

  @Test
  void mustSolveNoErrorWhenHostIsFoundButAddressIsNot() {

    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();
    final var wildcardHostName = HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME, IP.Version.IPV6);

    doReturn(EntryTemplates.acmeA())
      .when(this.solver)
      .findEntryTo(eq(wildcardHostName))
    ;

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    final var msg = res.getMessage();
    assertEquals(Rcode.NOERROR, msg.getRcode());
    assertEquals("", Messages.detailedPrint(msg));

  }
}
