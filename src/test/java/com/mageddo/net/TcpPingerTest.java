package com.mageddo.net;

import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class TcpPingerTest {

  @Test
  void mustPingSpecifiedPort() throws Exception {

    // arrange
    final var server = SocketUtils.createServerOnRandomPort();
    final var address = (InetSocketAddress) server.getLocalSocketAddress();

    try (server) {
      // act
      final var success = TcpPinger.ping(address, 1000);

      // assert
      assertTrue(success);
    }

  }

  @Test
  void wontBeAbleToPingUdpServer() throws Exception {

    // arrange
    final var server = new DatagramSocket(0);
    final var address = (InetSocketAddress) server.getLocalSocketAddress();

    try (server) {
      // act
      final var success = TcpPinger.ping(address, 1000);

      // assert
      assertFalse(success);
    }

  }
}
