package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;

import javax.inject.Singleton;

@Singleton
public class MultiSourceConfigDAOCmdArgs implements MultiSourceConfigDAO {

  private static String[] args;

  @Override
  public Config find() {
    return toConfig(this.findRaw());
  }

  public ConfigFlag findRaw() {
    return ConfigFlag.parse(args);
  }

  @Override
  public int priority() {
    return 3;
  }

  public static void setArgs(String[] args) {
    MultiSourceConfigDAOCmdArgs.args = args;
  }

  static Config toConfig(ConfigFlag config) {
    throw new UnsupportedOperationException();
  }
  static String[] getArgs() {
    return args;
  }
}
