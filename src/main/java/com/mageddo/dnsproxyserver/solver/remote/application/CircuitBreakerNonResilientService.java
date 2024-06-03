package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitBreakerService;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

public class CircuitBreakerNonResilientService implements CircuitBreakerService {
  @Override
  public Result safeHandle(final InetSocketAddress resolverAddress, Supplier<Result> sup) {
    return sup.get();
  }
}
