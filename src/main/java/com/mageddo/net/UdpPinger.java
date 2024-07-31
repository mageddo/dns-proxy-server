package com.mageddo.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * credits to: https://github.com/awadalaa/Socket-Programming-Java/blob/5d07915640e337eb491ca11922ce19eb580c6bef/UDP-Pinger/PingClient.java
 */
public class UdpPinger {

  public static boolean ping(InetSocketAddress address, Duration timeout) {
    try {
      pingWithResponse(address, timeout);
      return true;
    } catch (UncheckedIOException e) {
      return false;
    }
  }

  public static byte[] pingWithResponse(InetSocketAddress address, Duration timeout) {

    final var pingDatagram = buildPingDatagram(address);

    try (var socket = new DatagramSocket()) {

      socket.setSoTimeout((int) timeout.toMillis());
      socket.send(pingDatagram);

      final var response = new DatagramPacket(new byte[1024], 1024);
      socket.receive(response);

      return response.getData();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  private static DatagramPacket buildPingDatagram(InetSocketAddress address) {
    final String pingStr = "PING " + System.nanoTime() + " \n";
    final byte[] buf = pingStr.getBytes();
    return new DatagramPacket(buf, buf.length, address);
  }

}
