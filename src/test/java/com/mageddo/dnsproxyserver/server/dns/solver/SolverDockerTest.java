package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.docker.ContainerSolvingService;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.templates.HostnameTemplates;
import com.mageddo.dnsproxyserver.templates.IpTemplates;
import com.mageddo.dnsproxyserver.templates.MessageTemplates;
import com.mageddo.net.IP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    final var ip = "0.0.0.0";
    final var hostname = HostnameQuery.ofWildcard(HostnameTemplates.ACME_HOSTNAME);

    doReturn(true)
      .when(this.dockerDAO)
      .isConnected()
    ;
    doReturn(ip)
      .when(this.containerSolvingService)
      .findBestHostIP(eq(hostname));

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);

    final var resText = res.toString();
    assertTrue(resText.contains(ip), resText);
    verify(this.containerSolvingService).findBestHostIP(hostname);
  }


  @Test
  void mustSolveQuadARecordQuery() {
    // arrange
    final var query = MessageTemplates.acmeQuadAQuery();
    final var ip = IpTemplates.LOCAL_EXTENDED_IPV6;

    doReturn(true)
      .when(this.dockerDAO)
      .isConnected()
    ;

    doReturn(ip)
      .when(this.containerSolvingService)
      .findBestHostIP(any());

    // act
    final var res = this.solver.handle(query);

    // assert
    assertNotNull(res);
    assertTrue(Responses.hasFlag(res, Flags.RA));
    final var resText = res.toString();
    assertTrue(resText.contains(ip), resText);
    verify(this.containerSolvingService).findBestHostIP(this.hostnameQueryCaptor.capture());

    final var v = this.hostnameQueryCaptor.getValue();
    assertEquals(IP.Version.IPV6, v.getVersion());
  }

}
