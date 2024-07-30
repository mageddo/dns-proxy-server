package com.mageddo.dnsproxyserver;

import com.mageddo.commons.exec.CommandLines;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class DpsStressTest {

  @Test
  void test() throws Exception {

    final var executor = Executors.newVirtualThreadPerTaskExecutor();
    try (executor) {
      this.doNRequestsFor(executor, 1_000, Duration.ofSeconds(20));
    }

  }

  void doNRequestsFor(ExecutorService executor, int requests, Duration duration) throws InterruptedException {
    final var stopWatch = StopWatch.createStarted();
    while (stopWatch.getTime() < duration.toMillis()) {
      stopWatch.split();
      final var tasks = this.buildBatchRequestTasks(requests);
      executor.invokeAll(tasks);
      log.info("status=done, requests={}, stepDur={}, totalDur={}", requests, stopWatch.getSplitTime(), stopWatch.getTime());
    }
  }

  private List<Callable<Object>> buildBatchRequestTasks(int requests) {
    return IntStream.range(1, requests)
      .boxed()
      .map(it -> this.requestRandomQueryToDps())
      .toList();

  }

  private Callable<Object> requestRandomQueryToDps() {
    return () -> {
      final var result = CommandLines.exec("dig %s %s %s", "host.docker", "@127.0.0.1", "-p5753");
      result.checkExecution();
      assertThat(result.getOutAsString(), containsString("status: NOERROR"));
      return null;
    };
  }
}
