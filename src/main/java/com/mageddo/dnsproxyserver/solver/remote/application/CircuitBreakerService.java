package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

public interface CircuitBreakerService {

  // fixme #533 esse padrão de strategy não é mais necessário aqui, foi movido para CircuitBreakerDelegate
  //  onde tem mais chances de reduzir duplicação
  Result safeHandle(final InetSocketAddress resolverAddress, Supplier<Result> sup);

  CircuitStatus findCircuitStatus(InetSocketAddress resolverAddress);
}
