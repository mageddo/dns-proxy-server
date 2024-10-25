package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.commons.circuitbreaker.CircuitIsOpenException;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor.Resilience4jStateTransitor;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.statetransitor.StateTransitor;
import com.mageddo.dnsproxyserver.solver.remote.mapper.Resilience4jStatusMapper;
import com.mageddo.json.JsonUtils;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

@Slf4j
public class CircuitBreakerDelegateCanaryRateThreshold implements CircuitBreakerDelegate {

  private final CircuitBreaker circuitBreaker;
  private final String name;

  public CircuitBreakerDelegateCanaryRateThreshold(CircuitBreaker circuitBreaker, String name) {
    this.circuitBreaker = circuitBreaker;
    this.name = name;
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
      log.trace("circuit={}, status={}, metrics={}", this, status, formatMetrics());
    }
    return status;
  }

  @Override
  public StateTransitor stateTransitor() {
    return new Resilience4jStateTransitor(this.circuitBreaker);
  }

  private String formatMetrics() {
    if (Boolean.getBoolean("mg.solverRemote.circuitBreaker.canaryRateThreshold.detailedMetrics")) {
      return JsonUtils.prettyWriteValueAsString(this.circuitBreaker.getMetrics());
    }
    return "";
  }

  @Override
  public void transitionToHalfOpenState() {
    this.circuitBreaker.transitionToHalfOpenState();
  }

  @Override
  public String toString() {
    return StringUtils.firstNonBlank(this.name, this.getClass().getSimpleName());
  }
}
