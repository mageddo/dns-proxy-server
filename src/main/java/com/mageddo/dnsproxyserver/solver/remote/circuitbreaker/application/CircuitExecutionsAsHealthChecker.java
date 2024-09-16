package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.util.function.Supplier;

public class CircuitExecutionsAsHealthChecker implements HealthChecker, CircuitBreakerDelegate {

  private final CircuitBreakerDelegate delegate;
  private Supplier<Result> lastCall = null;

  public CircuitExecutionsAsHealthChecker(CircuitBreakerDelegate delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean isHealthy() {
    try {
      this.lastCall.get();
      return true;
    } catch (CircuitCheckException e) {
      return false;
    }
  }

  @Override
  public Result execute(Supplier<Result> sup) {
    this.lastCall = sup;
    return this.delegate.execute(sup);
  }

  @Override
  public CircuitStatus findStatus() {
    return this.delegate.findStatus();
  }
}
