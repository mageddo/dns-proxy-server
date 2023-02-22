package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates;
import com.mageddo.dnsproxyserver.templates.docker.NetworkTemplates;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
class DockerServiceTest {

  @InjectMock
  DockerDAO dockerDAO;

  @InjectMock(convertScopes = true)
  DockerNetworkDAO dockerNetworkDAO;

  @Inject
  DockerService dockerService;

  @BeforeEach
  void beforeEach() {
    doReturn("192.168.15.1")
      .when(this.dockerDAO)
      .findHostMachineIpRaw()
    ;
  }

  @Test
  void mustSolveSpecifiedNetworkFirst() {
    // arrange
    final var inspect = InspectContainerResponseTemplates.withDpsLabel();

    // act
    final var ip = this.dockerService.findBestIpMatch(inspect);

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
  void mustPreferBridgeNetworkOverOtherNetworksWhenThereIsNotABetterMatch() {
    // arrange

    final var bridgeNetwork = "custom-bridge";
    final var overlayNetwork = "shibata";

    final var inspect = InspectContainerResponseTemplates.withCustomBridgeAndOverylayNetwork();
    doReturn(NetworkTemplates.withOverlayDriver(overlayNetwork))
      .when(this.dockerNetworkDAO)
      .findNetwork(eq(overlayNetwork))
    ;
    doReturn(NetworkTemplates.withBridgeDriver(bridgeNetwork))
      .when(this.dockerNetworkDAO)
      .findNetwork(eq(bridgeNetwork))
    ;

    // act
    final var ip = this.dockerService.findBestIpMatch(inspect);

    // assert
    assertNotNull(ip);
    assertEquals("172.17.0.4", ip);

  }
}
