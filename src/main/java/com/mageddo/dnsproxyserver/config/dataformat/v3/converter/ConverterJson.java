package com.mageddo.dnsproxyserver.config.dataformat.v3.converter;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;

import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigMapper;

import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3JsonMapper;

import lombok.RequiredArgsConstructor;


@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConverterJson implements Converter {
  @Override
  public Config of(String raw) {
    return ConfigMapper.of(ConfigV3JsonMapper.of(raw), Config.Source.JSON);
  }

  @Override
  public String to(Config config) {
    return ConfigV3JsonMapper.toJson(ConfigMapper.toV3(config));
  }
}
