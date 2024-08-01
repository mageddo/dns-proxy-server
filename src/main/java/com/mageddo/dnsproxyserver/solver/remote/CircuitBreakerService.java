package com.mageddo.dnsproxyserver.solver.remote;

import com.mageddo.dnsproxyserver.solver.remote.application.CircuitStatus;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

public interface CircuitBreakerService {

  Result safeHandle(final InetSocketAddress resolverAddress, Supplier<Result> sup);

  CircuitStatus getCircuitStatus(InetSocketAddress resolverAddress);
}
