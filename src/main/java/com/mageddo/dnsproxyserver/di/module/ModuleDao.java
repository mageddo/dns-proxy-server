package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.dataprovider.MutableConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.MutableConfigDAOJson;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacade;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacadeDefault;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.docker.dataprovider.DockerNetworkFacadeDefault;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.*;
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
  MutableConfigDAO configDAO(MutableConfigDAOJson impl);

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
