package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.concurrent.ThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class TCPServer {

  public static final int MAX_CLIENT_ALIVE_SECS = 5;
  private final ScheduledExecutorService pool = ThreadPool.create(10);
  private final Set<WeakReference<SocketClient>> clients = new LinkedHashSet<>();

  public void start(int port, InetAddress address, SocketClientMessageHandler handler) {
    log.info("status=startingTcpServer, port={}", port);
    this.pool.submit(() -> this.start0(port, address, handler));
    this.pool.scheduleWithFixedDelay(this::watchDog, MAX_CLIENT_ALIVE_SECS, MAX_CLIENT_ALIVE_SECS, TimeUnit.SECONDS);
  }

  void start0(int port, InetAddress address, SocketClientMessageHandler handler) {
    try (var server = new ServerSocket(port)) {

      Socket socket;
      while (!server.isClosed() && (socket = server.accept()) != null) {
        final var client = new SocketClient(socket, handler);
        this.clients.add(new WeakReference<>(client));
        this.pool.submit(client);
      }

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  void watchDog() {
    try {
      final var itr = this.clients.iterator();
      if (this.clients.isEmpty()) {
        log.trace("status=no-clients");
        return;
      }
      final var clientsBefore = this.clients.size();
      while (itr.hasNext()) {
        try {
          final var client = itr.next().get();
          if (client == null) {
            log.debug("status=clientWasGarbageCollected");
            itr.remove();
            continue;
          }
          MDC.put("clientId", String.valueOf(client.getId()));
          if (client.isClosed()) {
            itr.remove();
            log.debug("status=removedAlreadyClosed, runningTime={}", client.getRunningTime());
          } else if (runningForTooLong(client)) {
            client.silentClose();
            itr.remove();
            log.debug("status=forcedRemove, runningTime={}", client.getRunningTime());
          }
        } finally {
          MDC.clear();
        }
      }
      log.debug(
        "status=watchdog, removed={}, clientsBefore={}, after={}",
        clientsBefore - this.clients.size(), clientsBefore, this.clients.size()
      );
    } catch (Throwable e) {
      log.error("status=watchdogFailed, msg={}", e.getMessage(), e);
    }
  }

  static boolean runningForTooLong(SocketClient client) {
    return Duration.ofSeconds(MAX_CLIENT_ALIVE_SECS).compareTo(client.getRunningTime()) <= 0;
  }
}
