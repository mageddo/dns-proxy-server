package com.mageddo.dnsproxyserver;

import com.mageddo.commons.exec.CommandLines;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class DpsStressTest {

  @Test
  void test() throws Exception {

    final var executor = Executors.newVirtualThreadPerTaskExecutor();

    try (executor) {

      final var tasks = this.buildBatchRequestTasks();
      executor.invokeAll(tasks);
    }

  }

  private List<Callable<Object>> buildBatchRequestTasks() {
    return IntStream.range(1, 1_000)
      .boxed()
      .map(it -> this.requestRandomQueryToDps())
      .toList();

  }

  private Callable<Object> requestRandomQueryToDps() {
    return () -> {
      CommandLines.exec("dig google.com", "@127.0.0.1", "-p5753");
      return null;
    };
  }
}
