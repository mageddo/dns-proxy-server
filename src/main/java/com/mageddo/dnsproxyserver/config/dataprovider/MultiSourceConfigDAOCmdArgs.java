package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;

import javax.inject.Singleton;

@Singleton
public class MultiSourceConfigDAOCmdArgs implements MultiSourceConfigDAO {

  private static String[] args;

  @Override
  public Config find() {
    return build(args);
  }

  @Override
  public int priority() {
    throw new UnsupportedOperationException();
  }

  public static void setArgs(String[] args) {
    MultiSourceConfigDAOCmdArgs.args = args;
  }

  public static Config build(String[] args) {
    final var config = ConfigFlag.parse(args);
    return toConfig(config);
  }

  private static Config toConfig(ConfigFlag config) {
    throw new UnsupportedOperationException();
  }
}
