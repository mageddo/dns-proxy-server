package com.mageddo.dnsproxyserver.config.configurer;

import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.dataformat.v3.file.MutableConfigDAOFile;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOFactoryBased;
import com.mageddo.dnsproxyserver.config.dataprovider.MutableConfigDAO;

import dagger.Binds;
import dagger.Module;

@Module
public interface ModuleConfigDAO {

  @Binds
  @Singleton
  MutableConfigDAO configDAO(MutableConfigDAOFile impl);

  @Binds
  @Singleton
  ConfigDAO configDao(ConfigDAOFactoryBased impl);

}
