package com.mageddo.dnsproxyserver.di;

import com.mageddo.dnsproxyserver.di.module.ModuleDao;
import com.mageddo.dnsproxyserver.di.module.ModuleDockerClient;
import com.mageddo.dnsproxyserver.di.module.ModuleHttpMapper;
import com.mageddo.dnsproxyserver.di.module.ModuleMain;
import com.mageddo.dnsproxyserver.di.module.ModuleSolver;
import com.mageddo.dnsproxyserver.di.module.ModuleStartup;
import com.mageddo.dnsproxyserver.docker.ContainerSolvingService;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.quarkus.QuarkusConfig;
import com.mageddo.dnsproxyserver.server.Starter;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import dagger.Component;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;
import java.util.Set;

@Singleton
@Component(modules = {
  ModuleMain.class,
  ModuleDao.class,
  ModuleDockerClient.class,
  QuarkusConfig.class,
  ModuleHttpMapper.class,
  ModuleSolver.class,
  ModuleStartup.class
})
public interface Context {

  static Context create() {
    return DaggerContext.create();
  }

  Starter starter();

  Set<StartupEvent> events();

  default void start() {
    this.events().forEach(StartupEvent::onStart);
    this.starter().start();
  }

  Instance<Solver> solvers();

  ContainerSolvingService containerSolvingService();

  DockerNetworkDAO dockerNetworkDAO();

  DockerDAO dockerDAO();
}
