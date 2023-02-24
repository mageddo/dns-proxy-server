package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.Threads;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class SocketClient implements Runnable, AutoCloseable {

  public static final long FPS_60 = (long) (1_000 / 60.0);
  private final UUID id;
  private final Socket socket;
  private final LocalDateTime createdAt;
  private final SocketClientMessageHandler handler;

  public SocketClient(Socket socket, SocketClientMessageHandler handler) {
    this.id = UUID.randomUUID();
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

  public void silentClose() {
    try {
      this.close();
      log.info("status=silent-closed, ranFor={}", this.getRunningTime());
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

  public InputStream getIn() {
    try {
      return this.socket.getInputStream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void run() {
    final var stopWatch = StopWatch.createStarted();
    try (final var in = this.socket.getInputStream()) {
      this.read(in);
    } catch (IOException e) {
      log.warn(
        "status=unexpected-client-close, client={}, runTime={}, msg={}",
        this, stopWatch.getTime(), e.getMessage(), e
      );
    } finally {
      log.debug("status=finalize-client, client={}, runTime={}", this, stopWatch.getTime());
      this.silentClose();
    }
  }

  void read(InputStream in) throws IOException {
    try {
      this.handler.handle(this);
    } catch (UnsupportedOperationException e) {
      this.readAsBytes(in);
    }
  }

  void readAsBytes(InputStream in) throws IOException {

    final var buff = new byte[512];
    while (this.isOpen()) {
      final var available = in.available();
      if (available == 0) {
        Threads.sleep(FPS_60);
        continue;
      }
      final var read = in.read(buff, 0, Math.min(available, buff.length));
      if (read == -1) {
        log.debug("status=streamEnded, time={}", this.getRunningTime());
        return;
      }
      this.handler.handle(buff, read, this);
    }
  }

  public boolean isOpen() {
    return !Thread.currentThread().isInterrupted()
      && this.socket.isConnected()
      && !this.socket.isClosed()
      && !this.socket.isInputShutdown()
      && !this.socket.isInputShutdown();
  }

  public String getId() {
    return String.valueOf(this.id).substring(0, 8) + this.getSocketAddress();
  }

  public SocketAddress getSocketAddress() {
    return this.socket.getRemoteSocketAddress();
  }

}

