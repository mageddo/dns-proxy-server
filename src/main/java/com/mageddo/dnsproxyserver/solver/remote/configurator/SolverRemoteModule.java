package com.mageddo.dnsproxyserver.solver.remote.configurator;

import com.mageddo.dnsproxyserver.solver.remote.CircuitBreakerService;
import com.mageddo.dnsproxyserver.solver.remote.application.CircuitBreakerFailSafeService;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface SolverRemoteModule {
  @Binds
  @Singleton
  CircuitBreakerService circuitBreakerService(CircuitBreakerFailSafeService impl);
}
