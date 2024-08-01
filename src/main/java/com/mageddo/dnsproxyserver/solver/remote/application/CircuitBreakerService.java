package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

public interface CircuitBreakerService {

  Result safeHandle(final InetSocketAddress resolverAddress, Supplier<Result> sup);

  CircuitStatus getCircuitStatus(InetSocketAddress resolverAddress);
}
