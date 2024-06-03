package com.mageddo.dnsproxyserver.solver.remote;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

public interface CircuitBreakerService {

  Result safeHandle(final InetSocketAddress resolverAddress, Supplier<Result> sup);

}
