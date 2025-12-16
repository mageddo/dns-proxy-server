package com.mageddo.dnsproxyserver.config.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.ConfigFactory;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOFactoryBased implements ConfigDAO {

  private final ConfigFactory configFactory;

  @Override
  public Config find() {
    return this.configFactory.find();
  }

}
