package com.mageddo.dnsproxyserver.config.configurer.di;


import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigService;
import com.mageddo.dnsproxyserver.config.configurer.di.module.ModuleConfigDAO;

import com.mageddo.dnsproxyserver.version.configurer.dagger.ModuleVersionConfigurer;

import dagger.Component;

@Singleton
@Component(modules = {ModuleConfigDAO.class, ModuleVersionConfigurer.class})
public interface Context {

  static Context create() {
    return DaggerContext.create();
  }

   ConfigService configService();
}
