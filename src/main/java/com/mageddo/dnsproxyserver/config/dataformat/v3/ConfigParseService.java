package com.mageddo.dnsproxyserver.config.dataformat.v3;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v3.converter.Converter;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3Mapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigParseService {

  private final List<Converter> unorderedConverters;
  private final ConfigMapper configMapper;

  public Config parseMerging() {
    final var converters = this.findConvertersSorted();
    final var configs = this.findConfigs(converters);
    return this.configMapper.mapFrom(configs);
  }

  private List<Config> findConfigs(List<Converter> converters) {
    return converters
      .stream()
      .map(Converter::parse)
      .map(ConfigV3Mapper::toConfig)
      .toList();
  }

  public List<Converter> findConvertersSorted() {
    throw new UnsupportedOperationException();
  }

}
