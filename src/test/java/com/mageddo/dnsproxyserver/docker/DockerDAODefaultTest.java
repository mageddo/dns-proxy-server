package com.mageddo.dnsproxyserver.docker;

import testing.templates.docker.NetworkTemplates;
import com.mageddo.net.IP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DockerDAODefaultTest {

  @Spy
  @InjectMocks
  DockerDAODefault dockerDAO;

  @Test
  void mustSolveIpv6IP() {

    // arrange
    final var version = IP.Version.IPV6;
    doReturn(NetworkTemplates.buildBridgeIpv4AndIpv6Network())
      .when(this.dockerDAO)
      .findBestNetwork(eq(version));

    // act
    final var ip = this.dockerDAO.findHostMachineIp(version);

    // assert
    assertNotNull(ip);
    assertEquals(IP.of("2001:db8:1::1"), ip);

  }

}
