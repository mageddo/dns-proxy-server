package com.mageddo.dnsserver.doh;

import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsserver.RequestHandler;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class DoHServer implements AutoCloseable {

  private final RequestHandler requestHandler;
  private Channel channel;
  private volatile boolean started;

  public void start(InetSocketAddress address) {

    synchronized (this) {
      if (this.started) {
        throw new IllegalStateException("Server already started");
      }
      this.started = true;
    }

    this.channel = DoHServerNetty.start(this.requestHandler, address);

    log.debug("status=starting, address={}", address);
  }

  @Override
  public void close() {
    if (!this.started) {
      return;
    }
    try {
      this.channel.closeFuture()
          .sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
