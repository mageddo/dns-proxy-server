package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3YamlMapper;

import lombok.NoArgsConstructor;

@NoArgsConstructor(onConstructor_ = @Inject)
public class YamlConfigDAO implements ConfigDAO {

  @Override
  public Config find() {
    return null;
  }

  public ConfigV3 parse(String yaml) {
    return ConfigV3YamlMapper.of(yaml);
  }

  @Override
  public int priority() {
    return 2;
  }

}
