package com.mageddo.dnsserver;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.commons.io.IoUtils;
import com.mageddo.dnsproxyserver.utils.Ips;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class TCPServer implements AutoCloseable {

  /**
   * See https://www.ietf.org/rfc/rfc1035.txt section 4.2.2
   */
  public static final Duration MAX_CLIENT_ALIVE_DURATION = Duration.ofMinutes(2);
  public static final int WATCHDOG_DELAY_SECS = 20;

  private final ExecutorService serverThreadPool;
  private final Set<WeakReference<SocketClient>> clients;
  private ServerSocket server;

  @Inject
  public TCPServer() {
    this.serverThreadPool = ThreadPool.newFixed(50);
    this.clients = new LinkedHashSet<>();
  }

  public void start(int port, InetAddress address, SocketClientMessageHandler handler) {
    log.debug("status=tcpServerStartScheduled, port={}", port);
    this.serverThreadPool.submit(() -> this.start0(port, address, handler));
    getGlobalScheduledThreadPool().scheduleWithFixedDelay(
      this::watchDog, WATCHDOG_DELAY_SECS, WATCHDOG_DELAY_SECS, TimeUnit.SECONDS
    );
  }

  private static ScheduledExecutorService getGlobalScheduledThreadPool() {
    return ThreadPool.scheduled();
  }

  void start0(int port, InetAddress address, SocketClientMessageHandler handler) {
    log.info("status=tcpServerStarting, port={}", port);
    final var addr = Ips.getAnyLocalAddress(); // todo porque isso funciona e sem passar o endereço nao?
    try (var server = this.server = new ServerSocket(port, 50, addr)) {

      Socket socket;
      while (!server.isClosed() && (socket = server.accept()) != null) {
        final var client = new SocketClient(socket, handler);
        this.clients.add(new WeakReference<>(client));
        this.serverThreadPool.submit(client);
      }

    } catch (Throwable e) {
      log.warn("status=tcpServerGetError, msg={}", e.getMessage(), e);
      throw new RuntimeException(e);
    } finally {
      log.debug("status=tcpServerClosing...");
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
            log.debug("status=forcedToClose, runningTime={}", client.getRunningTime());
          }
        } finally {
          MDC.clear();
        }
      }
      log.debug(
        "status=watchdog, removed={}, before={}, actual={}",
        clientsBefore - this.clients.size(), clientsBefore, this.clients.size()
      );
    } catch (Throwable e) {
      log.error("status=watchdogFailed, msg={}", e.getMessage(), e);
    }
  }

  static boolean runningForTooLong(SocketClient client) {
    return MAX_CLIENT_ALIVE_DURATION.compareTo(client.getRunningTime()) <= 0;
  }

  public void stop() {
    IoUtils.silentClose(this.server);
  }

  @Override
  public void close() throws Exception {
    this.stop();
    this.serverThreadPool.close();
  }
}
