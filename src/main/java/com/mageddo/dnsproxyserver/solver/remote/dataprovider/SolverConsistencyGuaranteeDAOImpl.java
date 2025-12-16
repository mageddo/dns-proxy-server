package com.mageddo.dnsproxyserver.solver.remote.dataprovider;

import com.mageddo.dnsproxyserver.solver.SolverCacheFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SolverConsistencyGuaranteeDAOImpl implements SolverConsistencyGuaranteeDAO {

  private final SolverCacheFactory solverCacheFactory;

  @Override
  public void flushCachesFromCircuitBreakerStateChange() {
    this.solverCacheFactory.scheduleCacheClear();
  }
}
