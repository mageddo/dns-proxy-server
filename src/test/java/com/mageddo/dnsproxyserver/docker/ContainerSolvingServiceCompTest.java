package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates;
import com.mageddo.dnsproxyserver.templates.docker.NetworkTemplates;
import com.mageddo.utils.dagger.TestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ContainerSolvingServiceCompTest {

  static TestContext ctx;

  DockerDAO dockerDAO;
  DockerNetworkDAO dockerNetworkDAO;
  ContainerSolvingService containerSolvingService;

  @BeforeAll
  static void beforeAll(){
    ctx = TestContext.create();
  }

  @BeforeEach
  void beforeEach() {

    this.containerSolvingService = ctx.containerSolvingService();
    this.dockerNetworkDAO = ctx.dockerNetworkDAO();
    this.dockerDAO = ctx.dockerDAO();
//    Mockito.reset();

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
  void mustPreferBridgeNetworkOverOtherNetworksWhenThereIsNotABetterMatch() {
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
}
