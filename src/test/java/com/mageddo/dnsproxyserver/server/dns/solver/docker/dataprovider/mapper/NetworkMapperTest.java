package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.docker.domain.Drivers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.docker.NetworkTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
