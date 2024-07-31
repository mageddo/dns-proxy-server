package com.mageddo.net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.IpTemplates;
import testing.templates.NetworkInterfaceTemplates;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class NetworksTest {

  static Network realNetwork;

  @Spy
  Network network;

  @BeforeAll
  static void beforeAll() {
    realNetwork = Networks.network;
  }

  @AfterAll
  static void afterAll() {
    Networks.network = realNetwork;
  }

  @BeforeEach
  void before() {
    Networks.network = this.network;
  }

  @Test
  void mustFindCurrentMachineIpAddress() {

    // arrange

    // act
    final var ip = Networks.findCurrentMachineIP();

    // assert
    assertNotNull(ip);
  }

  @Test
  void mustContainsLocalhostAddress() {
    // arrange

    // act
    final var ips = Networks.findMachineIps().toString();

    // assert
    assertTrue(ips.contains("127"), ips);
  }

  @Test
  void mustPreferNonLoopbackAddresses() {
    // arrange
    doReturn(NetworkInterfaceTemplates.localAndLoopback())
      .when(this.network).findNetworkInterfaces();

    // act
    final var ip = Networks.findCurrentMachineIP();

    // assert
    assertNotNull(ip);
    assertFalse(ip.isLoopback());
    assertEquals(IpTemplates.LOCAL_192, ip.toText());
  }

  @Test
  void mustPingSpecifiedPort() throws Exception {

    // arrange
    final var server = SocketUtils.createServerOnRandomPort();
    final var address = (InetSocketAddress) server.getLocalSocketAddress();

    try (server) {
      // act
      final var success = Networks.ping(address, 1000);

      // assert
      assertTrue(success);
    }

  }

}
