package com.mageddo.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestsTest {

  @BeforeEach
  void beforeEach() {
    Tests.resetCache();
  }

  @Test
  void mustCacheInTestCalls() {

    for (int i = 0; i < 3; i++) {
      assertTrue(Tests.inTest());
    }

    assertEquals(1, Tests.getHotCallsStat());
  }

  @Test
  void mustBeJunitTest() {
    assertTrue(Tests.inTestHotLoad());
  }

  @Test
  void hotCallsAreNotCached() {
    for (int i = 0; i < 3; i++) {
      assertTrue(Tests.inTestHotLoad());
    }
    assertEquals(Tests.getHotCallsStat(), 3);
  }

  @Test
  void mustBeJunitTestEvenWhenRunningInBackground() throws Exception {
    try (final var executor = Executors.newThreadExecutor()) {
      final var task = executor.submit(Tests::inTestHotLoad);
      assertTrue(task.get());
    }
  }
}
