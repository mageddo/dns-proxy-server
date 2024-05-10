package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.ContainerSolvingService;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import com.mageddo.net.IP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;
import testing.templates.HostnameTemplates;
import testing.templates.MessageTemplates;
import testing.templates.docker.EntryTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SolverDockerTest {

  @Mock
  ContainerSolvingService containerSolvingService;

  @Mock
  DockerDAO dockerDAO;

  @Captor
  ArgumentCaptor<HostnameQuery> hostnameQueryCaptor;

  SolverDocker solver;

  @BeforeEach
  void beforeEach() {
    this.solver = new SolverDocker(this.containerSolvingService, this.dockerDAO);
  }

  @Test
  void mustSolveExactHostname() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();
    final var entry = EntryTemplates.zeroIp();
    final var hostname = HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME);

    doReturn(true)
      .when(this.dockerDAO)
      .isConnected()
    ;
    doReturn(entry)
      .when(this.containerSolvingService)
      .findBestMatch(eq(hostname));

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);

    final var resText = res.toString();
    assertTrue(resText.contains(entry.getIp().toText()), resText);
    verify(this.containerSolvingService).findBestMatch(hostname);
  }

  @Test
  void mustSolveQuadARecordQuery() {
    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();
    final var entry = EntryTemplates.localIpv6();

    doReturn(true)
      .when(this.dockerDAO)
      .isConnected()
    ;

    doReturn(entry)
      .when(this.containerSolvingService)
      .findBestMatch(any());

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    assertTrue(Responses.hasFlag(res, Flags.RA));
    final var resText = res.toString();
    assertTrue(resText.contains(entry.getIp().toText()), resText);
    verify(this.containerSolvingService).findBestMatch(this.hostnameQueryCaptor.capture());

    final var v = this.hostnameQueryCaptor.getValue();
    assertEquals(IP.Version.IPV6, v.getVersion());
  }

  @Test
  void mustSolveEmptyIpWhenHostnameMatchesButNoIpIsFound() {
    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();
    final var entry = EntryTemplates.hostnameMatchedButNoAddress();

    doReturn(true)
      .when(this.dockerDAO)
      .isConnected()
    ;

    doReturn(entry)
      .when(this.containerSolvingService)
      .findBestMatch(any());

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    assertTrue(Responses.hasFlag(res, Flags.RA));
    assertEquals(Type.AAAA, Messages.findQuestionType(res.getMessage()));
    assertEquals("", Messages.detailedPrint(res.getMessage()));
  }

  @Test
  void mustReturnNxDomainWhenHostnameDONTMatches() {
    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();
    final var entry = EntryTemplates.hostnameNotMatched();

    doReturn(true)
      .when(this.dockerDAO)
      .isConnected()
    ;

    doReturn(entry)
      .when(this.containerSolvingService)
      .findBestMatch(any());

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNull(res);
  }

}
