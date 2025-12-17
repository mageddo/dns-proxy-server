package com.mageddo.dnsproxyserver.config.dataformat.v3.converter;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3YamlMapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConverterYaml implements Converter {
  @Override
  public Config of(String raw) {
    return ConfigMapper.of(ConfigV3YamlMapper.of(raw), Config.Source.YAML);
  }

  @Override
  public String to(Config config) {
    return ConfigV3YamlMapper.toYaml(ConfigMapper.toV3(config));
  }
}
