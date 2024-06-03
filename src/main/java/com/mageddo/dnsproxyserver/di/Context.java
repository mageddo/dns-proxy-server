package com.mageddo.dnsproxyserver.di;

import com.mageddo.di.CDIImpl;
import com.mageddo.dnsproxyserver.config.di.module.ModuleConfigDAO;
import com.mageddo.dnsproxyserver.di.module.ModuleDao;
import com.mageddo.dnsproxyserver.di.module.ModuleDockerClient;
import com.mageddo.dnsproxyserver.di.module.ModuleHttpMapper;
import com.mageddo.dnsproxyserver.di.module.ModuleMain;
import com.mageddo.dnsproxyserver.di.module.ModuleMap;
import com.mageddo.dnsproxyserver.di.module.ModuleSolver;
import com.mageddo.dnsproxyserver.di.module.ModuleStartup;
import com.mageddo.dnsproxyserver.solver.remote.configurator.SolverRemoteModule;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.quarkus.QuarkusConfig;
import com.mageddo.dnsproxyserver.server.Starter;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.docker.application.ContainerSolvingService;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;
import dagger.Component;
import jdk.jfr.Name;
import org.apache.commons.lang3.Validate;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

@Singleton
@Component(modules = {
  ModuleMain.class,
  ModuleDao.class,
  ModuleDockerClient.class,
  QuarkusConfig.class,
  ModuleHttpMapper.class,
  ModuleSolver.class,
  ModuleStartup.class,
  ModuleMap.class,
  ModuleConfigDAO.class,
  SolverRemoteModule.class
})
public interface Context {

  static Context create() {
    final var context = DaggerContext.create();
    CDI.setCDIProvider(() -> new CDIImpl(context));
    return context;
  }

  Starter starter();

  Set<StartupEvent> events();

  default void start() {
    this.events().forEach(StartupEvent::onStart);
    this.starter().start();
  }

  default <T> T get(Class<T> clazz) {
    final var v = bindings().get(clazz);
    Validate.notNull(v, "Bean not found for class: %s", clazz.getName());
    return (T) v.get();
  }

  Instance<Solver> solvers();

  ContainerSolvingService containerSolvingService();

  DockerNetworkFacade dockerNetworkDAO();

  DockerDAO dockerDAO();

  @Name("bindings")
  Map<Class<?>, Provider<Object>> bindings();

  default void stop() {
    this.starter().stop();
  }
}
