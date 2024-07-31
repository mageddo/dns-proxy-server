package com.mageddo.net;

import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.dnsserver.UDPServer;
import org.junit.jupiter.api.Test;
import testing.templates.MessageTemplates;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UdpPingerTest {

  @Test
  void wontPingUnavailableServer() throws Exception {

    // arrange
    final var address = IpAddrs.toInetSocketAddress(IpAddr.of("8.8.8.8", 5359));
    final var success = UdpPinger.ping(address, Duration.ofMillis(1000));
    assertFalse(success);

  }

  @Test
  void mustBeAbleToPingUdpServer() throws Exception {

    // arrange
    final var addr = Ips.getAnyLocalAddress(SocketUtils.findRandomFreePort());
    final var server = new UDPServer(addr, (query, kind) -> MessageTemplates.acmeAResponse());

    try (server) {

      server.start();

      // act
      final var success = Networks.ping(addr, 1000);

      // assert
      assertTrue(success);
    }

  }
}
