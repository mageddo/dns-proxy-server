package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.config.ConfigDAOJson;
import com.mageddo.dnsproxyserver.docker.ContainerDAO;
import com.mageddo.dnsproxyserver.docker.ContainerDAODefault;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.docker.DockerDAODefault;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAODefault;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public interface ModuleDao {

  @Binds
  @Singleton
  DockerDAO dockerDAO(DockerDAODefault impl);

  @Binds
  @Singleton
  DockerNetworkDAO dockerNetworkDAO(DockerNetworkDAODefault impl);

  @Binds
  @Singleton
  ConfigDAO configDAO(ConfigDAOJson impl);

  @Binds
  @Singleton
  ContainerDAO containerDAO(ContainerDAODefault impl);

  @Binds
  @Singleton
  Iface2 iface2(Iface2Impl impl);

  @Provides
  static Iface ifaceimpl(ConfigDAO configDAO){
    return new IfaceImpl();
  }

  public static class IfaceImpl implements Iface {

  }




}
