package com.mageddo.dnsproxyserver.config.di;


import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.config.di.module.ModuleConfigDAO;
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
