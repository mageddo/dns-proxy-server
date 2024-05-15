package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAO;
import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigService {

  private final Instance<MultiSourceConfigDAO> configDAOS;

  public Config findCurrentConfig(){
    this.configDAOS.stream()
      .toList()
  }
}
