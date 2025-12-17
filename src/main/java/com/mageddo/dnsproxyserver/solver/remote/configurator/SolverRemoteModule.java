package com.mageddo.dnsproxyserver.solver.remote.configurator;

import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAO;
import com.mageddo.dnsproxyserver.solver.remote.dataprovider.SolverConsistencyGuaranteeDAOImpl;

import dagger.Binds;
import dagger.Module;

@Module
public interface SolverRemoteModule {

  @Binds
  @Singleton
  SolverConsistencyGuaranteeDAO consistencyGuaranteeDAO(SolverConsistencyGuaranteeDAOImpl impl);
}
