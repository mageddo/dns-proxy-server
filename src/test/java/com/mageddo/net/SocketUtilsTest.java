package com.mageddo.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SocketUtilsTest {

  @Test
  void mustFindRandomFreePort(){
    // arrange

    // act
    final var port = SocketUtils.findRandomFreePort();

    // assert
    assertTrue(port > 0);
  }
}
