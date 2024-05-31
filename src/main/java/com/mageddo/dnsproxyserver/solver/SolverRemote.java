package com.mageddo.dnsproxyserver.solver;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.net.IpAddr;
import com.mageddo.net.IpAddrs;
import com.mageddo.net.NetExecutorWatchdog;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.CircuitBreakerOpenException;
import dev.failsafe.Failsafe;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
  private final Map<InetSocketAddress, CircuitBreaker<Result>> circuitBreakerMap = new ConcurrentHashMap<>();
  private final NetExecutorWatchdog netWatchdog = new NetExecutorWatchdog();
  private final ConfigService configService;
  private String status;

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
    req.splitStopWatch();
    final var circuitBreaker = this.circuitBreakerFor(req.getResolverAddress());
    try {
      return Failsafe
        .with(circuitBreaker)
        .get((ctx) -> this.queryResultWhilePingingResolver(req));
    } catch (CircuitCheckException | CircuitBreakerOpenException e) {
      final var clazz = ClassUtils.getSimpleName(e);
      log.debug("status=circuitEvent, server={}, type={}", req.getResolverAddress(), clazz);
      this.status = String.format("%s for %s", clazz, req.getResolverAddress());
      return Result.empty();
    }
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

  CircuitBreaker<Result> circuitBreakerFor(InetSocketAddress address) {
    final var config = this.findCircuitBreakerConfig();
    return this.circuitBreakerMap.computeIfAbsent(address, inetSocketAddress -> buildCircuitBreaker(config));
  }

  static CircuitBreaker<Result> buildCircuitBreaker(com.mageddo.dnsproxyserver.config.CircuitBreaker config) {
    return CircuitBreaker.<Result>builder()
      .handle(CircuitCheckException.class)
      .withFailureThreshold(config.getFailureThreshold(), config.getFailureThresholdCapacity())
      .withSuccessThreshold(config.getSuccessThreshold())
      .withDelay(config.getTestDelay())
      .build();
  }

  com.mageddo.dnsproxyserver.config.CircuitBreaker findCircuitBreakerConfig() {
    return this.configService.findCurrentConfig()
      .getSolverRemote()
      .getCircuitBreaker();
  }

  String getStatus() {
    return this.status;
  }

  @Override
  public void close() throws Exception {
    this.netWatchdog.close();
  }

  @Value
  @Builder
  static class Request {

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
      return this.getResolver().getAddress();
    }

    public void splitStopWatch() {
      this.stopWatch.split();
    }

    public CompletableFuture<Message> sendQueryAsyncToResolver() {
      return this.resolver.sendAsync(this.query).toCompletableFuture();
    }

    public long getElapsedTimeInMs() {
      return this.stopWatch.getTime() - this.stopWatch.getSplitTime();
    }

    public long getTime() {
      return this.stopWatch.getTime();
    }
  }

  @Value
  @Builder
  static class Result {

    private Response successResponse;
    private Message errorMessage;

    public static Result empty() {
      return Result.builder().build();
    }

    public static Result fromErrorMessage(Message message) {
      return builder().errorMessage(message).build();
    }

    public static Result fromSuccessResponse(Response res) {
      return Result.builder().successResponse(res).build();
    }

    public boolean hasSuccessMessage() {
      return this.successResponse != null;
    }

    public boolean hasErrorMessage() {
      return this.errorMessage != null;
    }

    public Response getErrorResponse() {
      return Optional.ofNullable(this.errorMessage)
        .map(Response::nxDomain)
        .orElse(null);
    }
  }
}
