package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.Threads;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class SocketClient implements Runnable, AutoCloseable {

  public static final long FPS = (long) (1_000 / 60.0);
  private final Socket socket;
  private final LocalDateTime createdAt;
  private final SocketClientMessageHandler handler;

  public SocketClient(Socket socket, SocketClientMessageHandler handler) {
    this.socket = socket;
    this.handler = handler;
    this.createdAt = LocalDateTime.now();
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

  public OutputStream getOut() {
    try {
      return this.socket.getOutputStream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void run() {
    try (final var in = this.socket.getInputStream()) {
      this.read(in);
    } catch (IOException e) {
      log.warn("status=unespected-client-close, msg={}", e.getMessage(), e);
      this.forceClose();
    }
  }

  void read(InputStream in) throws IOException {
    final var buff = new byte[512];
    while (this.isOpen()) {
      final var available = in.available();
      if (available == 0) {
        Threads.sleep(FPS);
        continue;
      }
      final var read = in.read(buff, 0, Math.min(available, buff.length));
      if (read == -1) {
        log.debug("status=stream-end, time={}", this.getRunningTime());
        return;
      }
      this.handler.handle(buff, this);
    }
  }


  private boolean isOpen() {
    return !Thread.currentThread().isInterrupted()
      && this.socket.isConnected()
      && !this.socket.isClosed()
      && !this.socket.isInputShutdown()
      && !this.socket.isInputShutdown();
  }


}
