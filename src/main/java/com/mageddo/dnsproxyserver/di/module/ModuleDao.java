package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.config.ConfigDAOJson;
import com.mageddo.dnsproxyserver.docker.ContainerDAO;
import com.mageddo.dnsproxyserver.docker.ContainerDAODefault;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.docker.DockerFacadeDefault;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacade;
import com.mageddo.dnsproxyserver.docker.DockerNetworkFacadeDefault;
import dagger.Binds;
import dagger.Module;

import javax.inject.Singleton;

@Module
public interface ModuleDao {

  @Binds
  @Singleton
  DockerFacade dockerDAO(DockerFacadeDefault impl);

  @Binds
  @Singleton
  DockerNetworkFacade dockerNetworkDAO(DockerNetworkFacadeDefault impl);

  @Binds
  @Singleton
  ConfigDAO configDAO(ConfigDAOJson impl);

  @Binds
  @Singleton
  ContainerDAO containerDAO(ContainerDAODefault impl);

}
