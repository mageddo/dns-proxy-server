package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;

public class EnvConverter implements Converter {
  @Override
  public ConfigV3 parse() {
    return null;
  }

  @Override
  public String serialize(ConfigV3 config) {
    return "";
  }

  @Override
  public int priority() {
    return 0;
  }
}
