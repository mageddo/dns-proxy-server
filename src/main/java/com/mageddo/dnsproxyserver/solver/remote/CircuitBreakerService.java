package com.mageddo.dnsproxyserver.solver.remote;

import java.util.function.Supplier;

public interface CircuitBreakerService {

  Result handle(Request req, Supplier<Result> sup);

}
