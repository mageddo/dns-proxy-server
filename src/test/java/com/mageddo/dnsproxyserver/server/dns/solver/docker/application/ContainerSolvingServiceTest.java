package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.server.dns.solver.docker.ContainerTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// todo #444
@ExtendWith(MockitoExtension.class)
class ContainerSolvingServiceTest {

  @Mock
  DockerFacade dockerFacade;

  @Mock
  DockerNetworkFacade networkDAO;

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

//  @Test
//  void mustReturnHostMachineIPWhenThereIsNoBetterMatch() {
//
//    // arrange
//    final var inspect = ngixWithDefaultBridgeNetworkOnly();
//    final var version = IP.Version.IPV6;
//    final var expectedIp = IP.of(IpTemplates.LOCAL_IPV6);
//
//    doReturn(expectedIp)
//      .when(this.dockerFacade)
//      .findHostMachineIp(eq(version))
//    ;
//
//    doReturn(true)
//      .when(this.containerSolvingService)
//      .isDockerSolverHostMachineFallbackActive()
//    ;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNotNull(ip);
//    assertEquals(expectedIp.toText(), ip);
//
//  }
//
//  @Test
//  void mustReturnNoIPWhenHostMachineFallbackIsDisabled() {
//
//    // arrange
//    final var inspect = ngixWithDefaultBridgeNetworkOnly();
//    final var version = IP.Version.IPV6;
//
//    doReturn(false)
//      .when(this.containerSolvingService)
//      .isDockerSolverHostMachineFallbackActive()
//    ;
//
//    // act
//    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);
//
//    // assert
//    assertNull(ip);
//    verify(this.dockerFacade, never()).findHostMachineIp(eq(version));
//
//  }

}
