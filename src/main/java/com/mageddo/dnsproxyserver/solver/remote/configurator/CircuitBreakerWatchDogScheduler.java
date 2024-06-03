package com.mageddo.dnsproxyserver.solver.remote.configurator;

import com.mageddo.commons.concurrent.ThreadPool;
import com.mageddo.dnsproxyserver.di.StartupEvent;
import com.mageddo.dnsproxyserver.solver.remote.application.CircuitBreakerFactory;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerWatchDogScheduler implements StartupEvent {

  private final ScheduledExecutorService executor = ThreadPool.scheduled(1);
  private final CircuitBreakerFactory circuitBreakerFactory;

  @Override
  public void onStart() {
    this.executor.scheduleWithFixedDelay(this.circuitBreakerFactory::checkCreatedCircuits, 0, 10, TimeUnit.SECONDS);
  }
}
