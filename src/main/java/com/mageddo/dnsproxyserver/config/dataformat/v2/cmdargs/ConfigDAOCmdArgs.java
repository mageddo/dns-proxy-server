package com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.mapper.ConfigFlagMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.vo.ConfigFlag;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOCmdArgs implements ConfigDAO {

  private static String[] args = new String[]{};

  @Override
  public Config find() {
    return ConfigFlagMapper.toConfig(this.findRaw());
  }

  public ConfigFlag findRaw() {
    return ConfigFlag.parse(args);
  }

  @Override
  public int priority() {
    return 3;
  }

  public static void setArgs(String[] args) {
    ConfigDAOCmdArgs.args = args;
  }

  static String[] getArgs() {
    return args;
  }
}
