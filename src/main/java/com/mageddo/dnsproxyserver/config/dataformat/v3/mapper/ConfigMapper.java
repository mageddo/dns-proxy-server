package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

public class ConfigMapper {
  public static Config of(ConfigV3 c) {
    return Config.builder()
        .log(mapLog(c))
        .build();
  }

  private static Log mapLog(ConfigV3 c) {
    return c.getLog();
  }

  public static ConfigV3 toV3(Config config) {
    throw new UnsupportedOperationException();
  }
}
