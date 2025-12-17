package com.mageddo.net;

import java.net.ServerSocket;

import lombok.SneakyThrows;

public class SocketUtils {

  @SneakyThrows
  public static int findRandomFreePort() {
    final var server = new ServerSocket(0);
    try (server) {
      return server.getLocalPort();
    }
  }

  @SneakyThrows
  public static ServerSocket createServerOnRandomPort() {
    return new ServerSocket(0);
  }
}
