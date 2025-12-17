package com.mageddo.net;

import org.junit.jupiter.api.Test;

import testing.templates.IpTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IPTest {

  @Test
  void mustBuildIpFromString() {
    // arrange
    final var ipStr = IpTemplates.LOCAL;

    // act
    final var ip = IP.of(ipStr);

    // assert
    assertEquals(ipStr, ip.toText());
  }
}
