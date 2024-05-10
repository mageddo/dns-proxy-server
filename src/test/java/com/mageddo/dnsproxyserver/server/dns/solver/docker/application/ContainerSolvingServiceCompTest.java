package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.di.Context;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import dagger.sheath.InjectMock;
import dagger.sheath.InjectSpy;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testing.templates.docker.InspectContainerResponseTemplates;
import testing.templates.docker.NetworkTemplates;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

// todo #444
@DaggerTest(component = Context.class)
class ContainerSolvingServiceCompTest {

  @InjectMock
  DockerFacade dockerFacade;

  @InjectMock
  DockerDAO dockerDAO;

  @InjectMock
  DockerNetworkFacade dockerNetworkDAO;

  @InjectMock
  MatchingContainerService matchingContainerService;

  @InjectSpy
  ContainerSolvingService containerSolvingService;

//  @Test
//  void mustSolveFromDefaultBridgeNetwork() {
//    // arrange
//    final var inspect = ngixWithDefaultBridgeNetworkOnly();
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect);
//
//    // assert
//    assertNotNull(ip);
//    assertEquals("172.17.0.4", ip);
//
//  }
//
//  @Test
//  void mustSolveEmptyIpv6FromDefaultBridgeNetwork() {
//    // arrange
//    final var inspect = ngixWithDefaultBridgeNetworkOnly();
//    final var version = IP.Version.IPV6;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNull(ip);
//
//  }
//
//  @Test
//  void mustSolveIpv6FromDefaultBridgeNetwork() {
//    // arrange
//    final var inspect = ngixWithIpv6DefaultBridgeNetworkOnly();
//    final var version = IP.Version.IPV6;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNotNull(ip);
//    assertEquals("2001:db8:abc1::242:ac11:4", ip);
//
//  }
//
//  @Test
//  void mustSolveIpv6FromAnyOtherNetwork() {
//    // arrange
//    final var inspect = ngixWithIpv6CustomBridgeNetwork();
//    final var version = IP.Version.IPV6;
//
//    doReturn(NetworkTemplates.withBridgeDriver("my-net1"))
//      .when(this.dockerNetworkDAO)
//      .findByName(anyString())
//    ;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNotNull(ip);
//    assertEquals(IpTemplates.LOCAL_IPV6, ip);
//
//  }
//
//  @Test
//  void mustSolveIpv6FromDefaultIPNetwork() {
//    // arrange
//    final var inspect = ngixWithIpv6DefaultIp();
//    final var version = IP.Version.IPV6;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNotNull(ip);
//    assertEquals("2001:db7:1::2", ip);
//    verify(this.dockerFacade, never()).findHostMachineIp();
//  }
//
//  @Test
//  void mustNotUseAnEmptyIpSpecifiedOnPreferredNetworks() {
//    // arrange
//    final var inspect = ngixWithIpv4DefaultBridgeAndIpv6CustomBridgeNetwork();
//    final var version = IP.Version.IPV6;
//
//    doReturn(NetworkTemplates.withBridgeDriver("my-net1"))
//      .when(this.dockerNetworkDAO)
//      .findByName(anyString())
//    ;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNotNull(ip);
//    assertEquals(IpTemplates.LOCAL_IPV6, ip);
//
//  }
//
//  @Test
//  void mustLeadWithNoneIPV6ReturnedFromDockerSolver() {
//    // arrange
//    final var hostnameQuery = HostnameQuery.of("nginx-2.dev", IP.Version.IPV6);
//    final var inspect = ngixWithDefaultBridgeNetworkOnly();
//
//    doReturn(List.of(inspect))
//      .when(this.matchingContainerService)
//      .findMatchingContainers(eq(hostnameQuery));
//
//    // act
//    final var ip = this.containerSolvingService.findBestMatch(hostnameQuery);
//
//    // assert
//    assertNotNull(ip);
//    assertFalse(ip.isHostnameMatched());
//    assertNull(ip.getIp());
//
//  }


}
