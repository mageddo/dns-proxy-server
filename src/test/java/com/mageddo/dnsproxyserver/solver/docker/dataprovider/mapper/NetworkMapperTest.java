package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.docker.domain.Drivers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.docker.NetworkTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testing.templates.docker.NetworkTemplates.buildBridgeIpv4AndIpv6Network;
import static testing.templates.docker.NetworkTemplates.buildBridgeIpv4OnlyNetwork;
import static testing.templates.docker.NetworkTemplates.buildHostNetworkWithNoIpam;

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

  @Test
  void mustLeadWhenNoIpamConfigIsAvailable(){

    // arrange
    final var dockerNetwork = buildHostNetworkWithNoIpam();

    // act
    final var network = NetworkMapper.of(dockerNetwork);

    // assert
    assertNotNull(network);
    assertFalse(network.hasAnyGateway());
    assertEquals("[]", network.getGateways().toString());
  }


  /**
   * see https://github.com/mageddo/dns-proxy-server/issues/481
   */
  @Test
  void mustExtractIpWhenASubnetIsSetAtIpv6TheGatewayIp(){

    // arrange
    final var dockerNetwork = NetworkTemplates.buildBridgeWithSubnetIPAtGatewayProp();

    // act
    final var network = NetworkMapper.of(dockerNetwork);

    // assert
    assertNotNull(network);
    assertTrue(network.hasAnyGateway());
    assertEquals("[172.19.0.1, fddb:21e4:36d4:2:0:0:0:1]", network.getGateways().toString());
  }

  @Test
  void mustMapFromCustomNetworkWithoutGateway(){

    // arrange
    final var dockerNetwork = NetworkTemplates.buildCustomIpv4NetworkWithoutGateway();

    // act
    final var network = NetworkMapper.of(dockerNetwork);

    // assert
    assertNotNull(network);
    assertFalse(network.hasAnyGateway());
  }

}
