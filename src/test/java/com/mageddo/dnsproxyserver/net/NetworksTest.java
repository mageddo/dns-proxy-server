package com.mageddo.dnsproxyserver.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworksTest {


  @Test
  void mustFindCurrentMachineIpAddress(){

    // arrange

    // act
    final var ip = Networks.findCurrentMachineIP();

    // assert
    assertNotNull(ip);
  }

  @Test
  void mustContainsLocalhostAddress(){
    // arrange

    // act
    final var ips = Networks.findMachineIps().toString();

    // assert
    assertTrue(ips.contains("127"), ips);
  }

}
