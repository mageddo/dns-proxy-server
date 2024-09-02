package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

// todo #533 criar um CircuitBreakerDelegate para esta classe
public class CircuitBreakerNonResilientService implements CircuitBreakerService {
  @Override
  public Result safeHandle(final InetSocketAddress resolverAddress, Supplier<Result> sup) {
    return sup.get();
  }

  @Override
  public CircuitStatus findCircuitStatus(InetSocketAddress resolverAddress) {
    return null;
  }
}
