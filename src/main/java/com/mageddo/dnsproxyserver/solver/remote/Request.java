package com.mageddo.dnsproxyserver.solver.remote;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.net.IpAddr;
import com.mageddo.net.IpAddrs;

import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
@Builder
public class Request {

  @NonNull
  Message query;

  @NonNull
  Resolver resolver;

  @NonNull
  Integer resolverIndex;

  @NonNull
  StopWatch stopWatch;

  public IpAddr getResolverAddr() {
    return IpAddrs.from(this.getResolverAddress());
  }

  public InetSocketAddress getResolverAddress() {
    return this.getResolver()
        .getAddress();
  }

  public void splitStopWatch() {
    this.stopWatch.split();
  }

  public CompletableFuture<Message> sendQueryAsyncToResolver(Executor executor) {
    log.trace("status=querying, server={}, req={}", this.resolver.getAddress(),
        Messages.simplePrint(this.query)
    );
    return this.resolver.sendAsync(this.query, executor)
        .toCompletableFuture();
  }

  public long getElapsedTimeInMs() {
    return this.stopWatch.getTime() - this.stopWatch.getSplitTime();
  }

  public long getTime() {
    return this.stopWatch.getTime();
  }
}
