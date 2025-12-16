package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import com.mageddo.dataformat.yaml.YamlUtils;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

public class YamlConfigDAO implements ConfigDAO {

  @Override
  public ConfigV3 find() {
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
