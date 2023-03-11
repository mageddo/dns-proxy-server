package com.mageddo.dnsproxyserver.di;

import com.mageddo.dnsproxyserver.quarkus.QuarkusConfig;
import com.mageddo.dnsproxyserver.server.Starter;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import dagger.Component;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    ModuleMain.class,
    ModuleDaos.class,
    ModuleDockerClient.class,
    QuarkusConfig.class,
    ModuleHttpMapper.class,
    ModuleSolver.class
})
public interface Context {

  static Context create() {
    return DaggerContext.create();
  }

  Starter starter();

  default void start() {
    this.starter().start();
  }

  Instance<Solver> solvers();
}
