package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.circuitbreaker.failsafe.CircuitStatusRefresh;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;

import java.util.function.Supplier;

public class CircuitBreakerDelegateFailsafe implements CircuitBreakerDelegate {

  private final CircuitBreaker<Result> circuitBreaker;

  public CircuitBreakerDelegateFailsafe(CircuitBreaker<Result> circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  public Result execute(Supplier<Result> sup) {
    return Failsafe
      .with(this.circuitBreaker)
      .get((ctx) -> sup.get());
  }

  @Override
  public CircuitStatus findStatus() {
    CircuitStatusRefresh.refresh(this.circuitBreaker);
    return CircuitBreakerStateMapper.fromFailSafeCircuitBreaker(this.circuitBreaker);
  }
}
