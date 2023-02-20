package com.mageddo.dnsproxyserver.server.dns;

public interface SocketClientMessageHandler {
  void handle(byte[] data, int length, SocketClient client);
}
