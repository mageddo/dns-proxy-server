package com.mageddo.net;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import testing.templates.IpAddrTemplates;

@ExtendWith(MockitoExtension.class)
class NetExecutorWatchdogTest {

  @Spy
  @InjectMocks
  NetExecutorWatchdog watchdog;

  @Test
  void mustCancelPingWhenFutureGetsDoneFirst() {
    // arrange
    final var ip = IpAddrTemplates.unknown();
    final var future = CompletableFuture.completedFuture(new Object());
    final var almostInfiniteTimeout = Integer.MAX_VALUE;

    // act
    this.watchdog.watch(ip, future, almostInfiniteTimeout);

    // assert
  }
}
