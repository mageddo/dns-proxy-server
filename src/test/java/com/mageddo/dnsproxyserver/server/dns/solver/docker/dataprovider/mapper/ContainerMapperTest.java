package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.docker.InspectContainerResponseTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static testing.templates.docker.InspectContainerResponseTemplates.ngixWithDefaultBridgeNetworkOnly;

class ContainerMapperTest {

  @Test
  void mustPutSpecifiedNetworkFirst(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.withDpsLabel();

    // act
    final var container = ContainerMapper.of(inspect);

    // assert
    assertNotNull(container);
    assertEquals("shibata", container.getFirstNetworkName());
  }

  @Test
  void mustMapBridgeNetwork() {

    // arrange
    final var inspect = ngixWithDefaultBridgeNetworkOnly();

    // act
    final var container = ContainerMapper.of(inspect);

    // assert
    assertNotNull(container);
    assertEquals("[172.17.0.4]", String.valueOf(container.getIps()));
    assertEquals("[shibata, dps, bridge]", String.valueOf(container.getPreferredNetworkNames()));

  }

  @Test
  void mustMapOverlayNetwork(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.withCustomBridgeAndOverlayNetwork();

    // act
    final var container = ContainerMapper.of(inspect);

    // assert
    assertNotNull(container);
    assertEquals("[dps, bridge]", String.valueOf(container.getPreferredNetworkNames()));
    assertEquals("[172.17.0.4]", String.valueOf(container.getIps()));
    assertEquals("[shibata, custom-bridge]", String.valueOf(container.getNetworksNames()));
  }
}
