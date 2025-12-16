package com.mageddo.dnsproxyserver.solver.remote.configurator;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.solver.remote.application.failsafe.CircuitBreakerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CircuitBreakerWatchDogScheduler implements StartupEvent {

  private final ScheduledExecutorService executor = ThreadPool.scheduled(1);
  private final CircuitBreakerFactory circuitBreakerFactory;

  @Override
  public void onStart() {
    this.executor.scheduleWithFixedDelay(this::logStats, 0, 10, TimeUnit.SECONDS);
  }

  void logStats() {
    this.circuitBreakerFactory
      .stats()
      .forEach(stats -> {
        log.debug("stats={}", stats);
      });
  }
}
