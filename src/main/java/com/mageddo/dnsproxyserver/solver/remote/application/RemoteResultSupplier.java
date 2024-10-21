package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.application.mapper.ResultMapper;
import com.mageddo.net.IpAddr;
import com.mageddo.net.NetExecutorWatchdog;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
public class RemoteResultSupplier implements ResultSupplier {

  public static final int PING_TIMEOUT_IN_MS = 1_500;

  private final Request req;
  private final Executor executor;
  private final NetExecutorWatchdog netWatchdog;

  public RemoteResultSupplier(Request req, Executor executor, NetExecutorWatchdog netWatchdog) {
    this.req = req;
    this.executor = executor;
    this.netWatchdog = netWatchdog;
  }

  @Override
  public Result get() {
    return this.queryResult(this.req);
  }

  Result queryResult(Request req) {
    final var resFuture = this.sendQueryAsyncToResolver(req);
    if (this.isPingWhileGettingQueryResponseActive()) {
      this.pingWhileGettingQueryResponse(req, resFuture);
    }
    return ResultMapper.from(resFuture, req);
  }

  CompletableFuture<Message> sendQueryAsyncToResolver(Request req) {
    return req.sendQueryAsyncToResolver(this.executor);
  }

  boolean isPingWhileGettingQueryResponseActive() {
    return Boolean.getBoolean("mg.solverRemote.pingWhileGettingQueryResponse");
  }

  void pingWhileGettingQueryResponse(Request req, CompletableFuture<Message> resFuture) {
    this.netWatchdog.watch(req.getResolverAddr(), resFuture, PING_TIMEOUT_IN_MS);
  }

  @Override
  public String toString() {
    return String.format("server=%s", this.req.getResolverAddr());
  }

  @Override
  public IpAddr getRemoteAddress() {
    return this.req.getResolverAddr();
  }
}
