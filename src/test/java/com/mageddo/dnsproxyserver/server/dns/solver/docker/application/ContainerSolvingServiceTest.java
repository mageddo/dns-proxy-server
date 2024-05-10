package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import com.mageddo.net.IP;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.IpTemplates;
import testing.templates.server.dns.solver.docker.ContainerTemplates;
import testing.templates.server.dns.solver.docker.NetworkTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContainerSolvingServiceTest {

  @Mock
  DockerDAO dockerDAO;

  @Mock
  DockerNetworkDAO dockerNetworkDAO;

  @Mock
  MatchingContainerService matchingContainerService;

  @Spy
  @InjectMocks
  ContainerSolvingService containerSolvingService;

  @Test
  void mustSolveSpecifiedNetworkFirst() {
    // arrange
    final var container = ContainerTemplates.withDpsLabel();

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(container);

    // assert
    assertNotNull(ip);
    assertEquals("172.23.0.2", ip);

  }

  @Test
  void mustReturnHostMachineIPWhenThereIsNoBetterMatch() {

    // arrange
    final var inspect = ContainerTemplates.withDefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;
    final var expectedIp = IP.of(IpTemplates.LOCAL_IPV6);

    doReturn(expectedIp)
      .when(this.dockerDAO)
      .findHostMachineIp(eq(version))
    ;

    doReturn(true)
      .when(this.containerSolvingService)
      .isDockerSolverHostMachineFallbackActive()
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNotNull(ip);
    assertEquals(expectedIp.toText(), ip);

  }

  @DisplayName("""
    When there is no a default bridge network but a custom, there is no dps network label,
    there is no a DPS network but there is a custom bridge network and a other like overlay, must prioritize to use
    the bridge network.
    """)
  @Test
  void mustPreferCustomBridgeNetworkOverOtherNetworksWhenThereIsNotABetterMatch() {
    // arrange
    final var bridgeNetwork = "custom-bridge";
    final var overlayNetwork = "shibata";

    final var container = ContainerTemplates.withCustomBridgeAndOverlayNetwork();
    doReturn(NetworkTemplates.withOverlayDriver(overlayNetwork))
      .when(this.dockerNetworkDAO)
      .findByName(eq(overlayNetwork))
    ;
    doReturn(NetworkTemplates.withBridgeDriver(bridgeNetwork))
      .when(this.dockerNetworkDAO)
      .findByName(eq(bridgeNetwork))
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(container);

    // assert
    assertNotNull(ip);
    assertEquals("172.17.0.8", ip);
    verify(this.dockerNetworkDAO, never()).findById(anyString());

  }

  @Test
  void mustReturnNoIPWhenHostMachineFallbackIsDisabled() {

    // arrange
    final var container = ContainerTemplates.withDefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;

    doReturn(false)
      .when(this.containerSolvingService)
      .isDockerSolverHostMachineFallbackActive()
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(container, version);

    // assert
    assertNull(ip);
    verify(this.dockerDAO, never()).findHostMachineIp(eq(version));

  }

  @Test
  void mustSolveFromDefaultBridgeNetwork() {
    // arrange
    final var container = ContainerTemplates.withDefaultBridgeNetworkOnly();

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(container);

    // assert
    assertNotNull(ip);
    assertEquals("172.17.0.4", ip);

  }

  @Test
  void mustSolveEmptyIpv6FromDefaultBridgeNetwork() {
    // arrange
    final var container = ContainerTemplates.withDefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(container, version);

    // assert
    assertNull(ip);

  }

  @Test
  void mustSolveIpv6FromDefaultBridgeNetwork() {
    // arrange
    final var container = ContainerTemplates.withIpv6DefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(container, version);

    // assert
    assertNotNull(ip);
    assertEquals("2001:db8:abc1:0:0:242:ac11:4", ip);

  }

}
