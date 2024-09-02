package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.util.function.Supplier;

public class CircuitBreakerDelegateNonResilient implements CircuitBreakerDelegate {

  @Override
  public Result execute(Supplier<Result> sup) {
    return sup.get();
  }

  @Override
  public CircuitStatus findStatus() {
    return null;
  }
}
