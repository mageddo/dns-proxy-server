package com.mageddo.dnsproxyserver.solver.dataprovider;

import com.mageddo.dnsproxyserver.solver.SolverCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SolverRemoteConsistencyGuaranteeDAOImpl implements SolverRemoteConsistencyGuaranteeDAO {

  private final SolverCacheFactory solverCacheFactory;

  @Override
  public void flushCachesFromCircuitBreakerStateChange(String previousStateName, String actualStateName) {
    this.solverCacheFactory.clearCaches();
    log.debug("status=clearedCache, previousStateName={}, actualStateName={}", previousStateName, actualStateName);
  }
}
