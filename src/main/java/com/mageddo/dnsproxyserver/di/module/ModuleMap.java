package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.docker.ContainerSolvingService;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.server.dns.ServerStarter;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverLocalDB;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * See
 * https://dagger.dev/dev-guide/multibindings
 * https://stackoverflow.com/questions/62150127/is-it-possible-to-get-beans-by-class-type-in-dagger2-similarly-to-spring-does
 *
 * todo check if {@link dagger.multibindings.Multibinds} can reduce this boilerplate.
 */
@Module
public interface ModuleMap {

  @Binds
  @IntoMap
  @ClassKey(DockerDAO.class)
  Object b1(DockerDAO bean);

  @Binds
  @IntoMap
  @ClassKey(ContainerSolvingService.class)
  Object b2(ContainerSolvingService bean);

  @Binds
  @IntoMap
  @ClassKey(DockerNetworkDAO.class)
  Object b3(DockerNetworkDAO bean);

  @Binds
  @IntoMap
  @ClassKey(ServerStarter.class)
  Object b4(ServerStarter bean);

  @Binds
  @IntoMap
  @ClassKey(SolverLocalDB.class)
  Object b5(SolverLocalDB bean);

  @Binds
  @IntoMap
  @ClassKey(ConfigDAO.class)
  Object b6(ConfigDAO bean);

}
