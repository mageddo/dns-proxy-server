package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;

import javax.inject.Singleton;

@Singleton
public class MultiSourceConfigDAOEnv implements MultiSourceConfigDAO {

  @Override
  public Config find() {
    return toConfig(this.findRaw());
  }

  public ConfigEnv findRaw() {
    return ConfigEnv.fromEnv();
  }

  @Override
  public int priority() {
    return 1;
  }

  private static Config toConfig(ConfigEnv config) {
    throw new UnsupportedOperationException();
  }

}
