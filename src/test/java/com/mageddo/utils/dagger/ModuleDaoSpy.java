package com.mageddo.utils.dagger;

import com.mageddo.dnsproxyserver.config.ConfigDAO;
import com.mageddo.dnsproxyserver.config.ConfigDAOJson;
import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.docker.DockerDAODefault;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.docker.DockerNetworkDAODefault;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import static org.mockito.Mockito.spy;

@Module
public interface ModuleDaoSpy {

  @Provides
  @Singleton
  static DockerDAO dockerDAO(DockerDAODefault impl){
    return spy(impl);
  }

  @Provides
  @Singleton
  static DockerNetworkDAO dockerNetworkDAO(DockerNetworkDAODefault impl){
    return spy(impl);
  }

  @Provides
  @Singleton
  static ConfigDAO configDAO(ConfigDAOJson impl){
    return spy(impl);
  }

}
