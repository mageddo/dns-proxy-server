package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.config.ConfigDAOJson;
import com.mageddo.dnsproxyserver.docker.ContainerFacade;
import com.mageddo.dnsproxyserver.docker.ContainerFacadeDefault;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacadeDefault;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerDAODefault;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAODefault;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAODefault;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface ModuleDao {

  @Binds
  @Singleton
  DockerNetworkFacade dockerNetworkFacade(DockerNetworkFacadeDefault impl);


  @Binds
  @Singleton
  ContainerFacade containerFacade(ContainerFacadeDefault impl);

  // ---------------- END:FACADE --------------- //

  @Binds
  @Singleton
  ConfigDAO configDAO(ConfigDAOJson impl);

  @Binds
  @Singleton
  ContainerDAO containerDAO(ContainerDAODefault impl);

  @Binds
  @Singleton
  DockerNetworkDAO dockerNetworkDAO(DockerNetworkDAODefault impl);

  @Binds
  @Singleton
  DockerDAO dockerDAO(DockerDAODefault impl);

}
