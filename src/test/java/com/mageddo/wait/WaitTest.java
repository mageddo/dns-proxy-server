package com.mageddo.wait;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaitTest {

  @Test
  void mustWaitUntilHappen() {

    final var expectedValue = 0;
    final var counter = new AtomicInteger(3);

    final var res = new Wait<>()
      .until(() -> counter.decrementAndGet() == expectedValue ? true : null);

    assertTrue(res);
    assertEquals(expectedValue, counter.get());
  }


  @Test
  void mustGetTimeoutWhenExpectationNeverHappen() {
    assertThrows(
      UnsatisfiedConditionException.class, () -> {
        new Wait<>().until(() -> null);
      })
    ;
  }
}
