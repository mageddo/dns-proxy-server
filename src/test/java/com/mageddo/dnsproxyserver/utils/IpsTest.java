package com.mageddo.dnsproxyserver.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpsTest {
  @Test
  void mustBeIpv4(){
    assertTrue(Ips.isIpv4("192.168.1.1"));
  }

  @Test
  void mustNotBeIpv4(){
    assertFalse(Ips.isIpv4("a.a.a.a"));
  }
}
