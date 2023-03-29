package com.mageddo.net;

import com.mageddo.dnsproxyserver.templates.IpTemplates;
import com.mageddo.dnsproxyserver.templates.NetworkInterfaceTemplates;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
  static void beforeAll(){
    realNetwork = Networks.network;
  }

  @AfterAll
  static void afterAll(){
    Networks.network = realNetwork;
  }

  @BeforeEach
  void before(){
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

}
