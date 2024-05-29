package com.mageddo.dnsserver;

/**
 * Lead and treat TCP Socket Client messages to something useful.
 */
public interface SocketClientMessageHandler {

  default void handle(SocketClient client) {
    throw new UnsupportedOperationException();
  }

  default void handle(byte[] data, int length, SocketClient client) {
    throw new UnsupportedOperationException();
  }
}
