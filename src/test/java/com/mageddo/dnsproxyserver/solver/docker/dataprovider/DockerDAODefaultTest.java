package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.mageddo.net.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.server.dns.solver.docker.NetworkTemplates;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DockerDAODefaultTest {

  @Spy
  @InjectMocks
  DockerDAODefault dockerDAO;

  @Test
  void mustSolveIpv6IP() {

    // arrange
    final var version = IP.Version.IPV6;
    doReturn(NetworkTemplates.withBridgeIpv4AndIpv6Network())
      .when(this.dockerDAO)
      .findBestNetwork(eq(version));

    // act
    final var ip = this.dockerDAO.findHostMachineIp(version);

    // assert
    assertNotNull(ip);
    assertEquals(IP.of("2001:db8:1::1"), ip);

  }

  @Test
  void mustRespectPriorityOrder() {

    // arrange
    final var version = IP.Version.IPV6;
    final var networks = List.of(
      NetworkTemplates.withOverlayIpv4AndIpv6Network(),
      NetworkTemplates.withBridgeIpv4AndIpv6Network()
    );

    doReturn(networks)
      .when(this.dockerDAO)
      .findNetworks();

    // act
    final var ip = this.dockerDAO.findHostMachineIp(version);

    // assert
    assertNotNull(ip);
    assertEquals(IP.of("2001:db8:1::1"), ip);

  }

  @Test
  void mustIgnoreNetworksWithoutIP() {

    // arrange
    final var version = IP.Version.IPV4;
    final var networks = List.of(
      NetworkTemplates.withHostDriverWithoutIps(),
      NetworkTemplates.withBridgeIpv4AndIpv6Network()
    );

    doReturn(networks)
      .when(this.dockerDAO)
      .findNetworks();

    // act
    final var ip = this.dockerDAO.findHostMachineIp(version);

    // assert
    assertNotNull(ip);
    assertEquals(IP.of("172.21.0.1"), ip);

  }


}
