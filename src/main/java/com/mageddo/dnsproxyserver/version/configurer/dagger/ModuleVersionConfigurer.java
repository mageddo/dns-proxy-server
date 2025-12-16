package com.mageddo.dnsproxyserver.version.configurer.dagger;

import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.version.VersionDAO;
import com.mageddo.dnsproxyserver.version.VersionDAOProp;
import dagger.Binds;
import dagger.Module;

@Module
public interface ModuleVersionConfigurer {
  @Binds
  @Singleton
  VersionDAO versionDAO(VersionDAOProp impl);
}
