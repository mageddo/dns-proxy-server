package com.mageddo.dnsproxyserver.server.dns;

public interface SocketClientMessageHandler {

  default void handle(SocketClient client) {
    throw new UnsupportedOperationException();
  }

  default void handle(byte[] data, int length, SocketClient client) {
    throw new UnsupportedOperationException();
  }
}
