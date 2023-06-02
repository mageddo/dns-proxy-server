package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.net.Networks;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.CircuitBreakerOpenException;
import dev.failsafe.Failsafe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SolverRemote implements Solver {

  public static final Duration DEFAULT_SUCCESS_TTL = Duration.ofMinutes(5);
  public static final Duration DEFAULT_NXDOMAIN_TTL = Duration.ofMinutes(60);

  static final String QUERY_TIMED_OUT_MSG = "Query timed out";
  static final long FPS_120 = 1000 / 120;

  private final RemoteResolvers delegate;
  private final Map<InetSocketAddress, CircuitBreaker<Response>> circuitBreakerMap = new ConcurrentHashMap<>();
  private final ExecutorService threadPool = ThreadPool.newFixed(50);
  private String status;

  @Override
  public Response handle(Message query) {
    final var stopWatch = StopWatch.createStarted();
    final var lastErrorMsg = new AtomicReference<Message>();
    for (int i = 0; i < this.delegate.resolvers().size(); i++) {

      stopWatch.split();
      final var resolver = this.delegate.resolvers().get(i);
      final var circuitBreaker = this.circuitBreakerFor(resolver.getAddress());
      final var idx = i;

      try {
        final var response = Failsafe
          .with(circuitBreaker)
          .get((ctx) -> this.handle0(idx, stopWatch, resolver, query, lastErrorMsg));
        if (response != null) {
          return response;
        }
      } catch (CircuitCheckException | CircuitBreakerOpenException e) {
        final var clazz = ClassUtils.getSimpleName(e);
        log.debug("status=circuitEvent, server={}, type={}", resolver.getAddress(), clazz);
        this.status = String.format("%s for %s", clazz, resolver.getAddress());
        continue;
      }
    }
    final var hasErrorResult = lastErrorMsg.get() == null;
    log.debug("status=finally, time={}, hasErrorResult={}", stopWatch.getTime(), hasErrorResult);
    if (hasErrorResult) {
      return null;
    }
    return Response.of(lastErrorMsg.get(), DEFAULT_NXDOMAIN_TTL);
  }

  private Response handle0(
    int i,
    StopWatch stopWatch,
    Resolver resolver,
    Message query,
    AtomicReference<Message> lastErrorMsg
  ) {

    final var resFuture = resolver.sendAsync(query).toCompletableFuture();
    final var address = resolver.getAddress();
    final var pingFuture = this.threadPool.submit(() -> Networks.ping(address.getAddress(), address.getPort(), 1_500));

    boolean mustCheckPing = true;
    while (true) {
      if (mustCheckPing && pingFuture.isDone()) {
        testPing(address, pingFuture);
        mustCheckPing = false;
      }
      if (resFuture.isDone()) {
        return this.treatResponse(i, resFuture, stopWatch, lastErrorMsg, query, resolver);
      }
      Threads.sleep(FPS_120);
    }

  }

  private static void testPing(InetSocketAddress address, Future<Boolean> pingFuture) {
    try {
      final var pingSuccess = pingFuture.get();
      log.info(
        "stats=pingTested, success={}, address={}:{}", pingSuccess, address.getAddress(), address.getPort()
      );
      if (!pingSuccess) {
        throw new CircuitCheckException(String.format(
          "Failed to ping: %s:%s", address.getAddress(), address.getPort()
        ));
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private Response treatResponse(
    int i,
    CompletableFuture<Message> resFuture,
    StopWatch stopWatch,
    AtomicReference<Message> lastErrorMsg,
    Message query,
    Resolver resolver) {
    try {
      final var res = Messages.setFlag(resFuture.get(), Flags.RA);
      if (res.getRcode() == Rcode.NOERROR) {
        log.trace(
          "status=found, i={}, time={}, req={}, res={}, server={}",
          i, stopWatch.getTime(), simplePrint(query), simplePrint(res), resolver
        );
        return Response.of(res, DEFAULT_SUCCESS_TTL);
      } else {
        lastErrorMsg.set(res);
        log.trace(
          "status=notFound, i={}, time={}, req={}, res={}, server={}",
          i, stopWatch.getTime(), simplePrint(query), simplePrint(res), resolver
        );
        return null;
      }
    } catch (InterruptedException | ExecutionException e) {
      if (e.getCause() instanceof IOException) {
        final var time = stopWatch.getTime() - stopWatch.getSplitTime();
        if (e.getMessage().contains(QUERY_TIMED_OUT_MSG)) {
          log.info(
            "status=timedOut, i={}, time={}, req={}, msg={} class={}",
            i, time, simplePrint(query), e.getMessage(), ClassUtils.getSimpleName(e)
          );
          throw new CircuitCheckException(e);
        }
        log.warn(
          "status=failed, i={}, time={}, req={}, server={}, errClass={}, msg={}",
          i, time, simplePrint(query), resolver, ClassUtils.getSimpleName(e), e.getMessage(), e
        );
        return null;
      }
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private CircuitBreaker<Response> circuitBreakerFor(InetSocketAddress address) {
    return this.circuitBreakerMap.computeIfAbsent(address, inetSocketAddress -> {
      final var breaker = CircuitBreaker.<Response>builder()
        .handle(CircuitCheckException.class)
        .withFailureThreshold(3, 10)
        .withSuccessThreshold(5)
        .withDelay(Duration.ofSeconds(20))
        .build();
      return breaker;
    });
  }

  String getStatus() {
    return this.status;
  }
}
