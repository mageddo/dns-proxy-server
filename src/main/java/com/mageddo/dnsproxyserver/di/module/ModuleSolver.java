package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.di.InstanceImpl;
import com.mageddo.dnsproxyserver.solver.CacheName;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.SolverCache;
import com.mageddo.dnsproxyserver.solver.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.SolverCachedRemote;
import com.mageddo.dnsproxyserver.solver.SolverDocker;
import com.mageddo.dnsproxyserver.solver.SolverLocalDB;
import com.mageddo.dnsproxyserver.solver.SolverSystem;
import com.mageddo.dnsproxyserver.solver.stub.SolverStub;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Set;

@Module
public interface ModuleSolver {

  @Provides
  @Singleton
  @ElementsIntoSet
  static Set<Solver> solvers(
    SolverSystem o1, SolverDocker o2, SolverLocalDB o3, SolverCachedRemote o4, SolverStub o5
  ) {
    return Set.of(o1, o2, o3, o4, o5);
  }

  @Provides
  static Instance<Solver> solversInstance(Set<Solver> instances){
    return new InstanceImpl<>(instances);
  }

  @Provides
  @Singleton
  @CacheName(name = Name.REMOTE)
  static SolverCache remoteCache(){
    return new SolverCache(Name.REMOTE);
  }

  @Provides
  @Singleton
  @CacheName(name = Name.GLOBAL)
  static SolverCache globalCache(){
    return new SolverCache(Name.GLOBAL);
  }
}
