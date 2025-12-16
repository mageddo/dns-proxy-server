package com.mageddo.dnsproxyserver.config.configurer;

import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOFactoryBased;

import dagger.Binds;
import dagger.Module;

@Module
public interface ModuleConfigDAO {

  @Binds
  ConfigDAO configDao(ConfigDAOFactoryBased impl);

}
