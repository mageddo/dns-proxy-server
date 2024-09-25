package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.mapper.Resilience4jStatusMapper;
import com.mageddo.json.JsonUtils;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class CircuitBreakerDelegateCanaryRateThreshold implements CircuitBreakerDelegate {

  private final CircuitBreaker circuitBreaker;

  public CircuitBreakerDelegateCanaryRateThreshold(CircuitBreaker circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  public Result execute(Supplier<Result> sup) {
    try {
      return this.circuitBreaker.executeSupplier(sup);
    } catch (CallNotPermittedException e) {
      throw new CircuitIsOpenException(e);
    }
  }

  @Override
  public CircuitStatus findStatus() {
    final var status = Resilience4jStatusMapper.toCircuitStatus(this.circuitBreaker.getState());
    if (log.isTraceEnabled()) {
      log.trace("status={}, metrics={}", status, JsonUtils.prettyWriteValueAsString(this.circuitBreaker.getMetrics()));
    }
    return status;
  }

  @Override
  public void transitionToHalfOpenState() {
    this.circuitBreaker.transitionToHalfOpenState();
  }
}
