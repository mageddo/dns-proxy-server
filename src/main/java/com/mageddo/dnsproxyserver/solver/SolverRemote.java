package com.mageddo.dnsproxyserver.solver;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.CircuitBreakerService;
import com.mageddo.net.NetExecutorWatchdog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.mageddo.dns.utils.Messages.simplePrint;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SolverRemote implements Solver, AutoCloseable {

  static final String QUERY_TIMED_OUT_MSG = "Query timed out";

  private final RemoteResolvers delegate;
  private final NetExecutorWatchdog netWatchdog = new NetExecutorWatchdog();
  private final CircuitBreakerService circuitBreakerService;

  @Override
  public Response handle(Message query) {
    final var stopWatch = StopWatch.createStarted();

    final var result = this.queryResultFromAvailableResolvers(query, stopWatch);
    log.debug(
      "status=finally, time={}, success={}, error={}",
      stopWatch.getTime(), result.hasSuccessMessage(), result.hasErrorMessage()
    );
    return Stream
      .of(result.getSuccessResponse(), result.getErrorResponse())
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  Result queryResultFromAvailableResolvers(Message query, StopWatch stopWatch) {
    final var lastErrorMsg = new AtomicReference<Message>();

    for (int i = 0; i < this.delegate.resolvers().size(); i++) {

      final var resolver = this.delegate.resolvers().get(i);
      final var request = this.buildRequest(query, i, stopWatch, resolver);

      final var result = this.safeQueryResult(request);
      if (result.hasSuccessMessage()) {
        return result;
      } else if (result.hasErrorMessage()) {
        lastErrorMsg.set(result.getErrorMessage());
      }

    }
    return Result.fromErrorMessage(lastErrorMsg.get());
  }

  Request buildRequest(Message query, int resolverIndex, StopWatch stopWatch, Resolver resolver) {
    return Request
      .builder()
      .resolverIndex(resolverIndex)
      .query(query)
      .stopWatch(stopWatch)
      .resolver(resolver)
      .build();
  }

  Result safeQueryResult(Request req) {
    return this.circuitBreakerService.handle(req, () -> this.queryResultWhilePingingResolver(req));
  }

  Result queryResultWhilePingingResolver(Request req) {
    final var resFuture = req.sendQueryAsyncToResolver();
    this.netWatchdog.watch(req.getResolverAddr(), resFuture);
    return this.transformToResult(resFuture, req);
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
        throw new CircuitCheckException(e);
      }
      log.warn(
        "status=failed, i={}, time={}, req={}, server={}, errClass={}, msg={}",
        request.getResolverIndex(), time, simplePrint(request.getQuery()), request.getResolverAddress(),
        ClassUtils.getSimpleName(e), e.getMessage(), e
      );
    } else {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public void close() throws Exception {
    this.netWatchdog.close();
  }

}
