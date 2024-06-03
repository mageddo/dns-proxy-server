package com.mageddo.dnsproxyserver.solver.dataprovider;

public interface SolverRemoteConsistencyGuaranteeDAO {
  void flushCachesFromCircuitBreakerStateChange(String stateName, String newStateName);
}
