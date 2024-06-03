package com.mageddo.dnsproxyserver.solver.remote.dataprovider;

public interface SolverConsistencyGuaranteeDAO {
  void flushCachesFromCircuitBreakerStateChange();
}
