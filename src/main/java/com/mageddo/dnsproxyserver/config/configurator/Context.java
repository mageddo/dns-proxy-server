package com.mageddo.dnsproxyserver.config.configurator;


import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.config.configurator.module.ModuleConfigDAO;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ModuleConfigDAO.class)
public interface Context {

  static Context create() {
    return DaggerContext.create();
  }

   ConfigService configService();
}
