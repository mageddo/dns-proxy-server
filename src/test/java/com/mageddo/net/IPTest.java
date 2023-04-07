package com.mageddo.net;

import testing.templates.IpTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IPTest {

  @Test
  void mustBuildIpFromString(){
    // arrange
    final var ipStr = IpTemplates.LOCAL;

    // act
    final var ip = IP.of(ipStr);

    // assert
    assertEquals(ipStr, ip.toText());
  }
}
