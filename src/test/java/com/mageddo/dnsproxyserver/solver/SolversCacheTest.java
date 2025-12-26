package com.mageddo.dnsproxyserver.solver;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.commons.concurrent.Threads;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.cache.SolverCache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;

import lombok.SneakyThrows;
import testing.templates.MessageTemplates;
import testing.templates.NamedResponseTemplates;
import testing.templates.ResponseTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SolversCacheTest {

  SolverCache cache = new SolverCache(Name.GLOBAL);

  @Test
  void mustLeadWithConcurrency() {

    // arrange
    final var req = MessageTemplates.acmeAQuery();
    final var r = new Random();

    // act
    concurrentRequests(1_000, req, r);

  }

  @Test
  void mustCacheForTheSpecifiedTime() {

    // arrange
    final var req = MessageTemplates.acmeAQuery();
    final var key = "A-acme.com";

    // act
    final var res = this.cache.handle(
        req,
        message -> NamedResponseTemplates.of(
            Response.of(Messages.aAnswer(message, "0.0.0.0"), Duration.ofMillis(50))
        )
    );

    // assert
    assertNotNull(res);
    assertNotNull(this.cache.get(key));

    Threads.sleep(res.getTTL()
        .plusMillis(10));
    assertNull(this.cache.get(key));
  }

  @Test
  void mustCacheAndGetValidResponse() {

    // arrange
    final var req = MessageTemplates.acmeAQuery();

    // act
    final var res = this.cache.handleToMsg(
        req,
        message -> NamedResponseTemplates.of(
            Response.internalSuccess(Messages.aAnswer(message, "0.0.0.0"))
        )
    );

    // assert
    assertNotNull(res);
    assertEquals(1, this.cache.getSize());

    final var header = res.getHeader();
    assertEquals(req.getHeader()
        .getID(), res.getHeader()
        .getID()
    );
    assertTrue(header.getFlag(Flags.QR));

  }

  @Test
  void cantCacheWhenDelegateSolverHasNoAnswer() {
    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = this.cache.handleToMsg(query, message -> null);

    // assert
    assertNull(res);
    assertEquals(0, this.cache.getSize());
  }

  @Test
  void mustEvictLocksAndDeadLocks() throws Exception {
    // arrange
    final var r = new SecureRandom();
    final Function<Message, NamedResponse> fn = message -> {
      Threads.sleep(r.nextInt(50) + 10);
      this.cache.clear();
      return NamedResponseTemplates.of(ResponseTemplates.acmeAResponse());
    };

    final var pool = ThreadPool.newFixed(3);

    // act
    this.runNTimes(
        it -> pool.submit(() -> this.cache.handleToMsg(
            MessageTemplates.randomHostnameAQuery(), fn
        )),
        30
    );

    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.SECONDS);

    // assert
    assertTrue(pool.isTerminated());
    assertTrue(pool.isShutdown());
    closePoolWhenItWontGetStuckByDeadlock(pool);
  }

  static void closePoolWhenItWontGetStuckByDeadlock(ExecutorService pool) {
    pool.close();
  }

  void runNTimes(final Consumer<Integer> task, final int times) {
    IntStream.range(0, times)
        .boxed()
        .forEach(task);
  }

  @SneakyThrows
  private void concurrentRequests(int quantity, Message req, Random r) {
    final var runnables = new ArrayList<Callable<Object>>();
    for (int i = 0; i < quantity; i++) {
      runnables.add(() -> this.handleRequest(req, r));
      if (i % 10 == 0) {
        runnables.add(() -> {
          this.cache.clear();
          return null;
        });
      }
    }

    try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      executor.invokeAll(runnables);
    }
  }

  Object handleRequest(Message req, Random r) {
    this.cache.handle(req, message -> {
          final var res = Response.internalSuccess(Messages.aAnswer(message, "0.0.0.0"));
          Threads.sleep(r.nextInt(10));
          return NamedResponseTemplates.of(res);
        }
    );
    return null;
  }

}
