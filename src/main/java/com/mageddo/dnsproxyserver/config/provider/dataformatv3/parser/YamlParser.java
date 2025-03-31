package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dataformat.yaml.YamlUtils;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;

public class YamlParser implements Parser {

  @Override
  public ConfigV3 parse() {
    return null;
  }

  public ConfigV3 parse(String yaml) {
    return YamlUtils.readValue(yaml, ConfigV3.class);
  }

  @Override
  public String marshal(ConfigV3 config) {
    return YamlUtils.writeValueAsString(config);
  }

  @Override
  public int priority() {
    return 2;
  }

}
