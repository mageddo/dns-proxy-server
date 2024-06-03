package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.CircuitBreakerService;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import dev.failsafe.CircuitBreakerOpenException;
import dev.failsafe.Failsafe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.Supplier;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFailSafeService implements CircuitBreakerService {

  private final CircuitBreakerFactory circuitBreakerFactory;

  private String status;

  @Override
  public Result handle(Request req, Supplier<Result> sup) {
    final var circuitBreaker = this.circuitBreakerFactory.createCircuitBreakerFor(req.getResolverAddress());
    try {
      return Failsafe
        .with(circuitBreaker)
        .get((ctx) -> sup.get());
    } catch (CircuitCheckException | CircuitBreakerOpenException e) {
      final var clazz = ClassUtils.getSimpleName(e);
      log.debug("status=circuitEvent, server={}, type={}", req.getResolverAddress(), clazz);
      this.status = String.format("%s for %s", clazz, req.getResolverAddress());
      return Result.empty();
    }
  }

  public String getStatus() {
    return this.status;
  }
}
