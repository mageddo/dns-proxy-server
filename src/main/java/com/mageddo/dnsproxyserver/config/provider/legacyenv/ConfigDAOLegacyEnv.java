package com.mageddo.dnsproxyserver.config.provider.legacyenv;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
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
