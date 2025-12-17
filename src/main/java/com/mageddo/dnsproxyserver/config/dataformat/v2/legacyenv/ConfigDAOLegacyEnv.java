package com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigDAO;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOLegacyEnv implements ConfigDAO {

  @Override
  public Config find() {
    return ConfigEnvMapper.toConfig(this.findRaw());
  }

  public ConfigEnv findRaw() {
    return ConfigEnv.fromEnv();
  }

  @Override
  public int priority() {
    return 1;
  }

}
