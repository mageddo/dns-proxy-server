package com.mageddo.dnsproxyserver.di.module;

import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.docker.ContainerDAO;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerDAOApi;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkDAOApi;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAODefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAODefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAODefault;

import dagger.Binds;
import dagger.Module;

@Module
public interface ModuleDao {

  @Binds
  @Singleton
  DockerNetworkDAO dockerNetworkFacade(DockerNetworkDAOApi impl);

  @Binds
  @Singleton
  ContainerDAO containerFacade(ContainerDAOApi impl);

  // ---------------- END:FACADE --------------- //

  @Binds
  @Singleton
  com.mageddo.dnsproxyserver.solver.docker.dataprovider.ContainerDAO containerDAO(com.mageddo.dnsproxyserver.solver.docker.dataprovider.ContainerDAODefault impl);

  @Binds
  @Singleton
  NetworkDAO dockerNetworkDAO(NetworkDAODefault impl);

  @Binds
  @Singleton
  DockerDAO dockerDAO(DockerDAODefault impl);

  @Binds
  @Singleton
  DpsContainerDAO dpsContainerDAO(DpsContainerDAODefault impl);

}
