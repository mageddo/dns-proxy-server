package com.mageddo.net;

import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpPinger {

  public static boolean ping(InetSocketAddress address, int timeout) {
    try (var socket = new Socket()) {
      socket.connect(address, timeout);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
