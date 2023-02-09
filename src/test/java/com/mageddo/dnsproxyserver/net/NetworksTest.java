package com.mageddo.dnsproxyserver.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NetworksTest {


  @Test
  void mustFindCurrentMachineIpAddress(){

    // arrange

    // act
    final var ip = Networks.findCurrentMachineIP();

    // assert
    assertNotNull(ip);
    assertFalse(ip.raw().startsWith("127"), ip.raw());
    System.out.println(ip);
  }

}
