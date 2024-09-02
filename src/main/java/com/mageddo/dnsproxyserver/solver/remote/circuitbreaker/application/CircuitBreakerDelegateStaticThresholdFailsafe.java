package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.circuitbreaker.failsafe.CircuitStatusRefresh;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.CircuitBreakerOpenException;
import dev.failsafe.Failsafe;

import java.util.function.Supplier;

public class CircuitBreakerDelegateStaticThresholdFailsafe implements CircuitBreakerDelegate {

  private final CircuitBreaker<Result> circuitBreaker;

  public CircuitBreakerDelegateStaticThresholdFailsafe(CircuitBreaker<Result> circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  public Result execute(Supplier<Result> sup) {
    try {
      return Failsafe
        .with(this.circuitBreaker)
        .get((ctx) -> sup.get());
    } catch (CircuitBreakerOpenException e){
      throw new CircuitIsOpenException(e);
    }
  }

  @Override
  public CircuitStatus findStatus() {
    CircuitStatusRefresh.refresh(this.circuitBreaker);
    return CircuitBreakerStateMapper.fromFailSafeCircuitBreaker(this.circuitBreaker);
  }
}
