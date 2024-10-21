package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.net.NetExecutorWatchdog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.mageddo.dns.utils.Messages.simplePrint;

@Slf4j
public class RemoteResultSupplier implements ResultSupplier {

  public static final int PING_TIMEOUT_IN_MS = 1_500;
  static final String QUERY_TIMED_OUT_MSG = "Query timed out";

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
    return this.transformToResult(resFuture, req);
  }

  CompletableFuture<Message> sendQueryAsyncToResolver(Request req) {
    return req.sendQueryAsyncToResolver(this.executor);
  }

  boolean isPingWhileGettingQueryResponseActive() {
    return Boolean.getBoolean("mg.solverRemote.pingWhileGettingQueryResponse");
  }

  Result transformToResult(CompletableFuture<Message> resFuture, Request request) {
    final var res = this.findFutureRes(resFuture, request);
    if (res == null) {
      return Result.empty();
    }

    if (Messages.isSuccess(res)) {
      log.trace(
        "status=found, i={}, time={}, req={}, res={}, server={}",
        request.getResolverIndex(), request.getTime(), simplePrint(request.getQuery()),
        simplePrint(res), request.getResolverAddress()
      );
      return Result.fromSuccessResponse(Response.success(res));
    } else {
      log.trace(
        "status=notFound, i={}, time={}, req={}, res={}, server={}",
        request.getResolverIndex(), request.getTime(), simplePrint(request.getQuery()),
        simplePrint(res), request.getResolverAddress()
      );
      return Result.fromErrorMessage(res);
    }
  }

  Message findFutureRes(CompletableFuture<Message> resFuture, Request request) {
    try {
      return Messages.setFlag(resFuture.get(), Flags.RA);
    } catch (InterruptedException | ExecutionException e) {
      this.checkCircuitError(e, request);
      return null;
    }
  }

  void checkCircuitError(Exception e, Request request) {
    if (e.getCause() instanceof IOException) {
      final var time = request.getElapsedTimeInMs();
      if (e.getMessage().contains(QUERY_TIMED_OUT_MSG)) {
        log.info(
          "status=timedOut, i={}, time={}, req={}, msg={} class={}",
          request.getResolverIndex(), time, simplePrint(request.getQuery()), e.getMessage(), ClassUtils.getSimpleName(e)
        );
        throw new CircuitCheckException(this.buildErrorMsg(e), e);
      }
      log.warn(
        "status=failed, i={}, time={}, req={}, server={}, errClass={}, msg={}",
        request.getResolverIndex(), time, simplePrint(request.getQuery()), request.getResolverAddress(),
        ClassUtils.getSimpleName(e), e.getMessage(), e
      );
    } else {
      throw new RuntimeException(this.buildErrorMsg(e), e);
    }
  }

  private String buildErrorMsg(Exception e) {
    return String.format("server=%s, msg=%s", this.req.getResolverAddr(), e.getMessage());
  }

  void pingWhileGettingQueryResponse(Request req, CompletableFuture<Message> resFuture) {
    this.netWatchdog.watch(req.getResolverAddr(), resFuture, PING_TIMEOUT_IN_MS);
  }

  @Override
  public String toString() {
    return String.format("server=%s", this.req.getResolverAddr());
  }
}
