package com.mageddo.dnsproxyserver.docker;

import org.junit.jupiter.api.Test;

import static com.mageddo.dnsproxyserver.templates.docker.NetworkTemplates.buildBridgeIpv4AndIpv6Network;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DockerNetworkServiceTest {

  @Test
  void mustSolveIpv4AddressEvenWhenIpv6IsAvailable(){

    // arrange
    final var network = buildBridgeIpv4AndIpv6Network();

    // act
    final var ip = DockerNetworkService.findGatewayIp(network);

    // assert
    assertEquals("172.21.0.1", ip);
  }
}
