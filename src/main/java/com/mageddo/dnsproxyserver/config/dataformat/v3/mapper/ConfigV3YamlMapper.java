package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dataformat.yaml.YamlUtils;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

public class ConfigV3YamlMapper {

  public static ConfigV3 of(String yaml) {
    return YamlUtils.readValue(yaml, ConfigV3.class);
  }

  public static String toYaml(ConfigV3 config) {
    return YamlUtils.writeValueAsString(config);
  }
}
