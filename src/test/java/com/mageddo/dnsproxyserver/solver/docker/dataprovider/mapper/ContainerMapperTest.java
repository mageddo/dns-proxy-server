package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.ContainerMapper;
import com.mageddo.net.IP;
import org.junit.jupiter.api.Test;
import testing.templates.docker.InspectContainerResponseTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static testing.templates.docker.InspectContainerResponseTemplates.ngixWithDefaultBridgeNetworkOnly;
import static testing.templates.docker.InspectContainerResponseTemplates.ngixWithIpv6CustomBridgeNetwork;
import static testing.templates.docker.InspectContainerResponseTemplates.ngixWithIpv6DefaultBridgeNetworkOnly;

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

  @Test
  void mustSolveIpv6FromDefaultBridgeNetwork() {
    // arrange
    final var inspect = ngixWithIpv6DefaultBridgeNetworkOnly();
    final var version = IP.Version.IPV6;

    // act
    final var container = ContainerMapper.of(inspect);

    // assert
    assertNotNull(container);

    final var network = container.getNetwork("bridge");
    assertNotNull(network);
    assertEquals("2001:db8:abc1:0:0:242:ac11:4", network.getIpAsText(version));

  }

  @Test
  void mustSolveIpv6FromAnyOtherNetworkWhenThereIsNoBetterMatch() {
    // arrange
    final var inspect = ngixWithIpv6CustomBridgeNetwork();

    // act
    final var container = ContainerMapper.of(inspect);

    // assert
    assertNotNull(container);
    assertEquals("[]", String.valueOf(container.getIps()));
    assertEquals("[my-net1]", String.valueOf(container.getNetworksNames()));

  }


}
