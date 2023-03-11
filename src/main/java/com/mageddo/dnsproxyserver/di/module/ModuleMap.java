package com.mageddo.dnsproxyserver.di.module;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module
public interface ModuleMap {

  @Binds
  @IntoMap
  @ClassKey(DockerDAO.class)
  Object dockerDAO(DockerDAO bean);
}
