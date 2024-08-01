package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.application.CircuitBreakerService;
import dev.failsafe.CircuitBreakerOpenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFailSafeService implements CircuitBreakerService {

  private final CircuitBreakerFactory circuitBreakerFactory;

  private String status;

  @Override
  public Result safeHandle(InetSocketAddress resolverAddress, Supplier<Result> sup) {
    try {
      return this.handle(resolverAddress, sup);
    } catch (CircuitCheckException | CircuitBreakerOpenException e) {
      final var clazz = ClassUtils.getSimpleName(e);
      log.debug("status=circuitEvent, server={}, type={}", resolverAddress, clazz);
      this.status = String.format("%s for %s", clazz, resolverAddress);
      return Result.empty();
    }
  }

  private Result handle(InetSocketAddress resolverAddress, Supplier<Result> sup) {
    return this.circuitBreakerFactory.check(resolverAddress, sup);
  }

  public String getStatus() {
    return this.status;
  }

  public void resetCircuitBreakerFactory() {
    this.circuitBreakerFactory.reset();
  }

  @Override
  public CircuitStatus findCircuitStatus(InetSocketAddress resolverAddress) {
    return this.circuitBreakerFactory.findStatus(resolverAddress);
  }
}
