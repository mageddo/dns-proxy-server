package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dataformat.yaml.YamlUtils;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;

public class YamlConverter implements Converter {

  @Override
  public ConfigV3 parse() {
    return null;
  }

  public ConfigV3 parse(String yaml) {
    return YamlUtils.readValue(yaml, ConfigV3.class);
  }

  @Override
  public String serialize(ConfigV3 config) {
    return YamlUtils.writeValueAsString(config);
  }

  @Override
  public int priority() {
    return 2;
  }

}
