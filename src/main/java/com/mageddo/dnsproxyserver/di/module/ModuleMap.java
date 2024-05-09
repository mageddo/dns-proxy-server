package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerSolvingAdapter;
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
  @ClassKey(DockerFacade.class)
  Object b1(DockerFacade bean);

  @Binds
  @IntoMap
  @ClassKey(ContainerSolvingAdapter.class)
  Object b2(ContainerSolvingAdapter bean);

  @Binds
  @IntoMap
  @ClassKey(DockerNetworkFacade.class)
  Object b3(DockerNetworkFacade bean);

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
