package com.mageddo.dnsproxyserver.di;

import com.mageddo.dnsproxyserver.server.dns.RequestHandler;
import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverDocker;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverLocalDB;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverRemote;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverSystem;
import com.mageddo.dnsproxyserver.server.dns.solver.SolversCache;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleMain {

  @ElementsIntoSet
  @Provides
  static Set<Solver> solvers(
      SolverDocker a, SolversCache b, SolverRemote c, SolverLocalDB d, SolverSystem e
  ) {
    return Set.of(a, (Solver) b, c, d, e);
  }

  @Provides
  static Instance<Solver> solversInstance(Set<Solver> solvers){
    return new InstanceImpl<>(solvers);
  }

  @Binds
  @Singleton
  RequestHandler configDAO(RequestHandlerDefault impl);

}
