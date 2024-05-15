package com.mageddo.dnsproxyserver.config.configurator;


import com.mageddo.dnsproxyserver.config.application.ConfigService;

import javax.inject.Singleton;

@Singleton
public interface Context {

  static Context create() {
    throw new UnsupportedOperationException();
  }

   ConfigService configService();
}
