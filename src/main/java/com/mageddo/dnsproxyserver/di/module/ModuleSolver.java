package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverDocker;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverLocalDB;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverRemote;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverSystem;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleSolver {
  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<Solver> solvers(
      SolverDocker o1, SolverRemote o3, SolverLocalDB o4, SolverSystem o5
  ) {
    return Set.of(o1, o3, o4, o5);
  }
}
