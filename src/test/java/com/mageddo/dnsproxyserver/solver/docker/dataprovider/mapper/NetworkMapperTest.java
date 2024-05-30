package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.docker.domain.Drivers;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.NetworkMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.docker.NetworkTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testing.templates.docker.NetworkTemplates.buildBridgeIpv4AndIpv6Network;
import static testing.templates.docker.NetworkTemplates.buildBridgeIpv4OnlyNetwork;

@ExtendWith(MockitoExtension.class)
class NetworkMapperTest {

  @Test
  void mustMapDockerNetworkInspectToNetworkDomainObject() {

    // arrange
    final var inspect = NetworkTemplates.buildBridgeIpv4AndIpv6Network();

    // act
    final var network = NetworkMapper.of(inspect);

    // assert
    assertEquals(Drivers.BRIDGE, network.getDriver());
    assertEquals("[172.21.0.1, 2001:db8:1:0:0:0:0:1]", String.valueOf(network.getGateways()));
    assertTrue(network.isIpv6Active());

  }

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

  @Test
  void mustLeadWhenNoIpv6IsReturned(){

    // arrange
    final var dockerNetwork = buildBridgeIpv4OnlyNetwork();

    // act
    final var network = NetworkMapper.of(dockerNetwork);

    // assert
    assertNotNull(network);
    assertEquals("[172.21.0.1]", network.getGateways().toString());
  }

}
