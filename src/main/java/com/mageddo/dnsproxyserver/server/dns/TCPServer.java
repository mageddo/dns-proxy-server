package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.ThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TCPServer {

  public static final int MAX_CLIENT_ALIVE_SECS = 5;
  private final ScheduledExecutorService pool = ThreadPool.create(10);
  private final List<SocketClient> clients = new ArrayList<>();

  public void start(int port, InetAddress address, SocketClientMessageHandler handler) {
    this.pool.submit(() -> this.start0(port, address, handler));
    this.pool.scheduleWithFixedDelay(this::watchDog, MAX_CLIENT_ALIVE_SECS, MAX_CLIENT_ALIVE_SECS, TimeUnit.SECONDS);
  }

  void start0(int port, InetAddress address, SocketClientMessageHandler handler) {
    try (var server = new ServerSocket(port)) {

      Socket socket;
      while (!server.isClosed() && (socket = server.accept()) != null) {
        this.pool.submit(new SocketClient(socket, handler));
      }

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  void watchDog() {
    final var itr = this.clients.iterator();
    while (itr.hasNext()) {
      final var client = itr.next();
      if (client.isClosed()) {
        itr.remove();
      } else if (runningForTooLong(client)) {
        client.forceClose();
        itr.remove();
      }
    }
  }

  static boolean runningForTooLong(SocketClient client) {
    return Duration.ofSeconds(MAX_CLIENT_ALIVE_SECS).compareTo(client.getRunningTime()) <= 0;
  }
}
