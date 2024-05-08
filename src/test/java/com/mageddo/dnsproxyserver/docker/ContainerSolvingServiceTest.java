package com.mageddo.dnsproxyserver.docker;

import com.mageddo.net.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.IpTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static testing.templates.docker.InspectContainerResponseTemplates.ngixWithDefaultBridgeNetworkOnly;


@ExtendWith(MockitoExtension.class)
class ContainerSolvingServiceTest {

  @Mock
  DockerDAO dockerDAO;

  @Mock
  DockerNetworkDAO networkDAO;

  @Mock
  MatchingContainerService matchingContainerService;

  @Spy
  @InjectMocks
  ContainerSolvingService containerSolvingService;

  @Test
  void mustReturnHostMachineIPWhenThereIsNoBetterMatch() {

    // arrange
    final var inspect = ngixWithDefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;
    final var expectedIp = IP.of(IpTemplates.LOCAL_IPV6);

    doReturn(expectedIp)
      .when(this.dockerDAO)
      .findHostMachineIp(eq(version))
    ;

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect, version);

    // assert
    assertNotNull(ip);
    assertEquals(expectedIp.toText(), ip);

  }

  @Test
  void mustReturnNoIPWhenHostMachineFallbackIsDisabled() {

    // arrange
    final var inspect = ngixWithDefaultBridgeNetworkOnly();

    // act
    final var ip = this.containerSolvingService.findBestIpMatch(inspect);

    // assert
    assertNotNull(ip);
    assertEquals("172.17.0.4", ip);

  }

}
