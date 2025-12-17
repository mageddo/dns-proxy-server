package com.mageddo.net;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.utils.InetAddresses;
import com.mageddo.utils.Executors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetExecutorWatchdog implements AutoCloseable {

  public static final int FPS_120 = 1000 / 120;

  private final ExecutorService threadPool = Executors.newThreadExecutor();

  /**
   * Will ping the #pingAddr while waiting the future to be done, which occurs first will return,
   * if ping fails, exception is thrown. "future.get()" won't be called.
   */
  public <T> CompletableFuture<T> watch(IpAddr pingAddr, CompletableFuture<T> future,
      int pingTimeoutInMs) {

    final var pingFuture = this.threadPool.submit(
        () -> Networks.ping(pingAddr.getIpAsText(), pingAddr.getPort(), pingTimeoutInMs)
    );

    boolean mustCheckPing = true;
    while (true) {
      if (mustCheckPing && pingFuture.isDone()) {
        this.checkConnection(pingFuture, InetAddresses.toSocketAddress(pingAddr));
        mustCheckPing = false;
      }
      if (future.isDone()) {
        pingFuture.cancel(true);
        return future;
      }
      Threads.sleep(FPS_120);
    }

  }

  void checkConnection(Future<Boolean> pingFuture, InetSocketAddress address) {
    try {
      final var pingSuccess = pingFuture.get();
      log.debug(
          "stats=pingTested, success={}, address={}:{}", pingSuccess, address.getAddress(),
          address.getPort()
      );
      if (!pingSuccess) {
        throw new CircuitCheckException(String.format(
            "Failed to ping address: %s:%s", address.getAddress(), address.getPort()
        ));
      }
    } catch (InterruptedException ignored) {
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws Exception {
    this.threadPool.close();
  }
}
