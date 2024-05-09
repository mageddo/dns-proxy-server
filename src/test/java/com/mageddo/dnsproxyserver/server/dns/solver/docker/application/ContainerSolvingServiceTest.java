// fixme
//package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;
//
//import com.mageddo.dnsproxyserver.docker.DockerFacade;
//import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
//import com.mageddo.net.IP;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import testing.templates.IpTemplates;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static testing.templates.docker.InspectContainerResponseTemplates.ngixWithDefaultBridgeNetworkOnly;
//
//// todo #444
//@ExtendWith(MockitoExtension.class)
//class ContainerSolvingServiceTest {
//
//  @Mock
//  DockerFacade dockerFacade;
//
//  @Mock
//  DockerNetworkFacade networkDAO;
//
//  @Mock
//  MatchingContainerService matchingContainerService;
//
//  @Spy
//  @InjectMocks
//  ContainerSolvingServiceTest containerSolvingService;
//
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
//
//}
