package com.mageddo.net.osx;

import org.apache.commons.exec.OS;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class NetworksTest {

  @BeforeAll
  static void beforeAll() {
    assumeTrue(OS.isFamilyMac());
  }

  @Test
  void mustListNetworks() {
    // arrange

    // act
    final var networksNames = Networks.findNetworksNames();

    // assert
    assertFalse(networksNames.isEmpty());
  }

}
