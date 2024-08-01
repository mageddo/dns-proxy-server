package com.mageddo.dnsproxyserver.solver.remote.configurator;

import com.mageddo.dnsproxyserver.solver.remote.application.CircuitBreakerService;
import com.mageddo.dnsproxyserver.solver.remote.application.failsafe.CircuitBreakerFailSafeService;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAOImpl;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface SolverRemoteModule {
  @Binds
  @Singleton
  CircuitBreakerService circuitBreakerService(CircuitBreakerFailSafeService impl);

  @Binds
  @Singleton
  SolverConsistencyGuaranteeDAO consistencyGuaranteeDAO(SolverConsistencyGuaranteeDAOImpl impl);
}
