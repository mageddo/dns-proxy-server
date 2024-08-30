package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.util.function.Supplier;

public interface CircuitBreakerDelegate {

  Result execute(Supplier<Result> sup);

  CircuitStatus findStatus();
}
