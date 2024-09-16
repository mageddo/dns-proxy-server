package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.commons.concurrent.Threads;
import com.mageddo.concurrent.ThreadsV2;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class CircuitBreakerDelegateSelfObservableCanaryRateThreshold implements CircuitBreakerDelegate, AutoCloseable {

  private final CircuitBreakerDelegateCanaryRateThreshold delegate;
  private final Duration sleepDuration;
  private final HealthChecker healthChecker;
  private boolean open = true;

  public CircuitBreakerDelegateSelfObservableCanaryRateThreshold(
    CircuitBreakerDelegateCanaryRateThreshold delegate, HealthChecker healthChecker
  ) {
    this(delegate, Duration.ofSeconds(1), healthChecker);
  }

  public CircuitBreakerDelegateSelfObservableCanaryRateThreshold(
    CircuitBreakerDelegateCanaryRateThreshold delegate, Duration sleepDuration, HealthChecker healthChecker
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

  private void startOpenCircuitHealthCheckWorker() {
    Thread
      .ofVirtual()
      .start(() -> {
        while (ThreadsV2.isNotInterrupted() && this.open) {
          Threads.sleep(this.sleepDuration);
          this.healthCheckWhenInOpenState();
        }
      });
  }

  private void healthCheckWhenInOpenState() {
    final var status = this.findStatus();
    if (!CircuitStatus.isOpen(status)) {
      log.trace("status=notOpenStatus, status={}", status);
      return;
    }
    final var success = this.isHealthy();
    if (success) {
      this.delegate.transitionToHalfOpenState();
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
