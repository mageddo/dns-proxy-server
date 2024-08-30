package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.application.failsafe.CircuitBreakerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerExecutorService {

  private final CircuitBreakerFactory factory;

  Result safeExecute(final InetSocketAddress resolverAddress, Supplier<Result> sup) {
    final var circuitBreaker = this.findCircuitBreaker(resolverAddress);
    return circuitBreaker.execute(sup);
  }

  CircuitStatus findCircuitStatusFor(InetSocketAddress resolverAddress) {
    return this.findCircuitBreaker(resolverAddress).findStatus();
  }

  private CircuitBreakerDelegate findCircuitBreaker(InetSocketAddress resolverAddress) {
    return this.factory.findCircuitBreaker(resolverAddress);
  }
}
