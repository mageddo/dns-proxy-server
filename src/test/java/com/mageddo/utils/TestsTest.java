package com.mageddo.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestsTest {
  @Test
  void mustBeJunitTest(){
    assertTrue(Tests.inTest());
  }

  @Test
  void mustBeJunitTestEvenWhenRunningInBackground() throws Exception {
    try(final var executor = Executors.newThreadExecutor()){
      final var task = executor.submit(Tests::inTest);
      assertTrue(task.get());
    }
  }
}
