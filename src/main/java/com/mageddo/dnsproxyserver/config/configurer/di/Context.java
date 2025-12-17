package com.mageddo.dnsproxyserver.config.configurer.di;


import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.ConfigService;
import com.mageddo.dnsproxyserver.config.configurer.ModuleConfigDAO;
import com.mageddo.dnsproxyserver.config.configurer.ModuleV2ConfigDAO;
import com.mageddo.dnsproxyserver.config.configurer.ModuleV3ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.file.ConfigFilePathDAO;
import com.mageddo.dnsproxyserver.version.configurer.dagger.ModuleVersionConfigurer;

import dagger.Component;

@Singleton
@Component(modules = {
    ModuleConfigDAO.class,
    ModuleV2ConfigDAO.class,
    ModuleV3ConfigDAO.class,
    ModuleVersionConfigurer.class
})
public interface Context {

  static Context create() {
    return DaggerContext.create();
  }

  ConfigService configService();

  ConfigFilePathDAO configFilePathDAO();
}
