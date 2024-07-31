package com.mageddo.utils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutorsTest {

  @Test
  @SneakyThrows
  void virtualThreadsActiveByDefault() {
    // arrange
    try (final var executor = Executors.newThreadExecutor()) {
      // act
      final var isVirtual = executor.submit(() -> Thread.currentThread().isVirtual()).get();

      // assert
      assertTrue(isVirtual);
    }

  }
}
