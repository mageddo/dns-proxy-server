package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthChecker;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class CircuitExecutionsAsHealthChecker implements HealthChecker, CircuitBreakerDelegate {

  private final CircuitBreakerDelegate delegate;
  private final boolean healthWhenNoCallTemplateToDo;
  private Supplier<Result> lastCall = null;

  public CircuitExecutionsAsHealthChecker(CircuitBreakerDelegate delegate) {
    this.delegate = delegate;
    this.healthWhenNoCallTemplateToDo = false;
  }

  @Override
  public boolean isHealthy() {
    try {
      if (this.lastCall == null) {
        log.trace("status=noLastCall, answer={}", this.healthWhenNoCallTemplateToDo);
        return this.healthWhenNoCallTemplateToDo;
      }
      final var res = this.lastCall.get();
      log.trace("status=delegateToLastCall, answer={}", res);
      return true;
    } catch (CircuitCheckException e) {
      log.trace("status=callFailed, answer=false, msg={}", e.getMessage());
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

  @Override
  public void transitionToHalfOpenState() {
    this.delegate.transitionToHalfOpenState();
  }
}
