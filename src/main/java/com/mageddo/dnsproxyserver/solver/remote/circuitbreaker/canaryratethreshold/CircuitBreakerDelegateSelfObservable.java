package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.concurrent.ThreadsV2;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthChecker;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class CircuitBreakerDelegateSelfObservable implements CircuitBreakerDelegate, AutoCloseable {

  private final CircuitBreakerDelegate delegate;
  private final Duration sleepDuration;
  private final HealthChecker healthChecker;
  private boolean open = true;

  public CircuitBreakerDelegateSelfObservable(
    CircuitBreakerDelegate delegate, HealthChecker healthChecker
  ) {
    this(delegate, Duration.ofSeconds(1), healthChecker);
  }

  public CircuitBreakerDelegateSelfObservable(
    CircuitBreakerDelegate delegate, Duration sleepDuration, HealthChecker healthChecker
  ) {
    this.delegate = delegate;
    this.sleepDuration = sleepDuration;
    this.healthChecker = healthChecker;
    this.startOpenCircuitHealthCheckWorker();
  }

  @Override
  public Result execute(Supplier<Result> sup) {
    return this.delegate.execute(sup);
  }

  @Override
  public CircuitStatus findStatus() {
    return this.delegate.findStatus();
  }

  @Override
  public void transitionToHalfOpenState() {
    this.delegate.transitionToHalfOpenState();
  }

  private void startOpenCircuitHealthCheckWorker() {
    Thread
      .ofVirtual()
      .start(() -> {
        while (this.shouldRun()) {
          Threads.sleep(this.sleepDuration);
          this.healthCheckWhenInOpenState();
        }
      });
  }

  private boolean shouldRun() {
    return ThreadsV2.isNotInterrupted() && this.open;
  }

  private void healthCheckWhenInOpenState() {
    final var status = this.findStatus();
    log.trace("status=checking, status={}", status);
    if (!CircuitStatus.isOpen(status)) {
      log.trace("status=notOpenStatus, status={}", status);
      return;
    }
    final var success = this.isHealthy();
    if (success) {
      this.transitionToHalfOpenState();
      log.debug("status=halfOpenStatus, circuitBreaker={}", this);
    }
  }

  private boolean isHealthy() {
    return this.healthChecker.isHealthy();
  }

  @Override
  public void close() throws Exception {
    this.open = false;
  }

}
