package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.dataprovider.PersistentConfigDAO;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacade;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.server.dns.DnsServerStarter;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverLocalDB;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
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
  @ClassKey(ContainerFacade.class)
  Object b1(ContainerFacade bean);

  @Binds
  @IntoMap
  @ClassKey(DockerNetworkFacade.class)
  Object b3(DockerNetworkFacade bean);

  @Binds
  @IntoMap
  @ClassKey(DnsServerStarter.class)
  Object b4(DnsServerStarter bean);

  @Binds
  @IntoMap
  @ClassKey(SolverLocalDB.class)
  Object b5(SolverLocalDB bean);

  @Binds
  @IntoMap
  @ClassKey(PersistentConfigDAO.class)
  Object b6(PersistentConfigDAO bean);

  @Binds
  @IntoMap
  @ClassKey(DockerDAO.class)
  Object b7(DockerDAO bean);

}
