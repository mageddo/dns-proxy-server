package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitBreakerService;
import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.util.function.Supplier;

public class CircuitBreakerNonResilientService implements CircuitBreakerService {
  @Override
  public Result handle(Request req, Supplier<Result> sup) {
    return sup.get();
  }
}
