package com.mageddo.net;

import lombok.SneakyThrows;

import java.net.ServerSocket;

public class SocketUtils {

  @SneakyThrows
  public static int findRandomFreePort() {
    final var server = new ServerSocket(0);
    try (server) {
      return server.getLocalPort();
    }
  }
}
