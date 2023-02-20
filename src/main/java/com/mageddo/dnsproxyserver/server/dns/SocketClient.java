package com.mageddo.dnsproxyserver.server.dns;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class SocketClient implements Runnable, AutoCloseable {

  private final Socket socket;
  private final LocalDateTime createdAt;

  public SocketClient(Socket socket) {
    this.socket = socket;
    this.createdAt = LocalDateTime.now();
  }

  @Override
  public void run() {

  }

  @Override
  public void close() throws Exception {
    this.socket.close();
  }

  public Duration getRunningTime() {
    return Duration.between(this.createdAt, LocalDateTime.now());
  }

  public boolean isClosed() {
    return this.socket.isClosed();
  }

  public void forceClose() {
    try {
      this.close();
      log.info("status=force-closed, ranFor={}", this.getRunningTime());
    } catch (Exception e) {
      log.warn("status=couldnt-close-client, msg={}", e.getMessage(), e);
    }
  }
}
