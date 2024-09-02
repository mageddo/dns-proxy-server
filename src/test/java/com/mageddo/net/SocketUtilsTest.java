package com.mageddo.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SocketUtilsTest {

  @Test
  void mustFindRandomFreePort() {
    // arrange

    // act
    final var port = SocketUtils.findRandomFreePort();

    // assert
    assertTrue(port > 0);
  }

  @Test
  void mustFindTwoDifferentPortsForConsecutiveCalls() {
    for (int i = 0; i < 3; i++) {
      assertNotEquals(SocketUtils.findRandomFreePort(), SocketUtils.findRandomFreePort());
    }
  }

}
