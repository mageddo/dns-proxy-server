package com.mageddo.dnsproxyserver.config.provider.cmdargs;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.provider.cmdargs.mapper.ConfigFlagMapper;
import com.mageddo.dnsproxyserver.config.provider.cmdargs.vo.ConfigFlag;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
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
