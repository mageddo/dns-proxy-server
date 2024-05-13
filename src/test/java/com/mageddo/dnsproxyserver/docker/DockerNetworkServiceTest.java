package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.NetworkMapper;
import org.junit.jupiter.api.Test;

import static testing.templates.docker.NetworkTemplates.buildBridgeIpv4AndIpv6Network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DockerNetworkServiceTest {

  @Test
  void mustSolveIpv4AddressEvenWhenIpv6IsAvailable(){

    // arrange
    final var network = buildBridgeIpv4AndIpv6Network();

    // act
    final var ip = NetworkMapper.findGatewayIp(network);

    // assert
    assertNotNull(ip);
    assertEquals("172.21.0.1", ip.toText());
  }
}
