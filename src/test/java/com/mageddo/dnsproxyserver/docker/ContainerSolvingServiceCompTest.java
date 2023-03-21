package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.dnsproxyserver.templates.IpTemplates;
import com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates;
import com.mageddo.dnsproxyserver.templates.docker.NetworkTemplates;
import dagger.sheath.InjectMock;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates.ngixWithDefaultBridgeNetworkOnly;
import static com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates.ngixWithIpv4DefaultBridgeAndIpv6CustomBridgeNetwork;
import static com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates.ngixWithIpv6CustomBridgeNetwork;
import static com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates.ngixWithIpv6DefaultBridgeNetworkOnly;
import static com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates.ngixWithIpv6DefaultIp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DaggerTest(component = Context.class)
class ContainerSolvingServiceCompTest {

  @InjectMock
  DockerDAO dockerDAO;

  @InjectMock
  DockerNetworkDAO dockerNetworkDAO;

  @Inject
  ContainerSolvingService containerSolvingService;

  @Test
  void mustSolveSpecifiedNetworkFirst() {
    // arrange
    final var inspect = InspectContainerResponseTemplates.withDpsLabel();

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect);

    // assert
    assertNotNull(ip);
    assertEquals("172.23.0.2", ip);

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

    final var inspect = InspectContainerResponseTemplates.withCustomBridgeAndOverylayNetwork();
    doReturn(NetworkTemplates.withOverlayDriver(overlayNetwork))
      .when(this.dockerNetworkDAO)
      .findByName(eq(overlayNetwork))
    ;
    doReturn(NetworkTemplates.withBridgeDriver(bridgeNetwork))
      .when(this.dockerNetworkDAO)
      .findByName(eq(bridgeNetwork))
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect);

    // assert
    assertNotNull(ip);
    assertEquals("172.17.0.4", ip);
    verify(this.dockerNetworkDAO, never()).findById(anyString());

  }

  @Test
  void mustSolveFromDefaultBridgeNetwork() {
    // arrange
    final var inspect = ngixWithDefaultBridgeNetworkOnly();

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect);

    // assert
    assertNotNull(ip);
    assertEquals("172.17.0.4", ip);

  }

  @Test
  void mustSolveEmptyIpv6FromDefaultBridgeNetwork() {
    // arrange
    final var inspect = ngixWithDefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNull(ip);

  }

  @Test
  void mustSolveIpv6FromDefaultBridgeNetwork() {
    // arrange
    final var inspect = ngixWithIpv6DefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNotNull(ip);
    assertEquals("2001:db8:abc1::242:ac11:4", ip);

  }

  @Test
  void mustSolveIpv6FromAnyOtherNetwork() {
    // arrange
    final var inspect = ngixWithIpv6CustomBridgeNetwork();
    final var version = IP.Version.IPV6;

    doReturn(NetworkTemplates.withBridgeDriver("my-net1"))
      .when(this.dockerNetworkDAO)
      .findByName(anyString())
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNotNull(ip);
    assertEquals(IpTemplates.LOCAL_IPV6, ip);

  }

  @Test
  void mustSolveIpv6FromDefaultIPNetwork() {
    // arrange
    final var inspect = ngixWithIpv6DefaultIp();
    final var version = IP.Version.IPV6;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNotNull(ip);
    assertEquals("2001:db7:1::2", ip);
    verify(this.dockerDAO, never()).findHostMachineIpRaw();
  }

  @Test
  void mustNotUseAnEmptyIpSpecifiedOnPreferredNetworks() {
    // arrange
    final var inspect = ngixWithIpv4DefaultBridgeAndIpv6CustomBridgeNetwork();
    final var version = IP.Version.IPV6;

    doReturn(NetworkTemplates.withBridgeDriver("my-net1"))
      .when(this.dockerNetworkDAO)
      .findByName(anyString())
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNotNull(ip);
    assertEquals(IpTemplates.LOCAL_IPV6, ip);

  }


}
