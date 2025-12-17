package com.mageddo.dnsproxyserver.solver.remote.application;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OnCacheMustBeFlushedEvent {

  private final SolverConsistencyGuaranteeDAO solverConsistencyGuaranteeDAO;

  public void run() {
    this.solverConsistencyGuaranteeDAO.flushCachesFromCircuitBreakerStateChange();
    log.debug("status=flushed");
  }
}
