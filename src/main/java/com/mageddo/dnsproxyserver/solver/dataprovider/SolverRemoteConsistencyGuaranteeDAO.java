package com.mageddo.dnsproxyserver.solver.dataprovider;

public interface SolverRemoteConsistencyGuaranteeDAO {
  void flushCachesFromCircuitBreakerStateChange();
}
