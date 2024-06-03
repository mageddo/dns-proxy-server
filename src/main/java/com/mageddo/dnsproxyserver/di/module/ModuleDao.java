package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.dataprovider.PersistentConfigDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.PersistentConfigDAOJson;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacade;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacadeDefault;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacadeDefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.ContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.ContainerDAODefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAODefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAODefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAODefault;
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
  PersistentConfigDAO configDAO(PersistentConfigDAOJson impl);

  @Binds
  @Singleton
  ContainerDAO containerDAO(ContainerDAODefault impl);

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
