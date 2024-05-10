package com.mageddo.dnsproxyserver.di;

import com.mageddo.dnsproxyserver.di.module.ModuleDao;
import com.mageddo.dnsproxyserver.di.module.ModuleDockerClient;
import com.mageddo.dnsproxyserver.di.module.ModuleHttpMapper;
import com.mageddo.dnsproxyserver.di.module.ModuleMain;
import com.mageddo.dnsproxyserver.di.module.ModuleMap;
import com.mageddo.dnsproxyserver.di.module.ModuleSolver;
import com.mageddo.dnsproxyserver.di.module.ModuleStartup;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.quarkus.QuarkusConfig;
import com.mageddo.dnsproxyserver.server.Starter;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.ContainerSolvingService;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import dagger.Component;
import jdk.jfr.Name;
import org.apache.commons.lang3.Validate;

import javax.enterprise.inject.Instance;
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
    ModuleMap.class
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

  default <T> T get(Class<T> clazz) {
    final var v = bindings().get(clazz);
    Validate.notNull(v, "Bean not found for class: %s", clazz.getName());
    return (T) v.get();
  }

  Instance<Solver> solvers();

  ContainerSolvingService containerSolvingService();

  DockerNetworkFacade dockerNetworkDAO();

  DockerDAO dockerDAO();

//  DockerFacadeMock dockerDAOMock(); FIXME

  @Name("bindings")
  Map<Class<?>, Provider<Object>> bindings();

  default void stop() {
    this.starter().stop();
  }
}
