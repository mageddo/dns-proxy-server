package com.mageddo.dnsproxyserver.config.dataprovider.v3;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.v3.converter.Converter;
import com.mageddo.dnsproxyserver.config.dataprovider.v3.mapper.ConfigMapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigV3Service {

  private final List<Converter> unorderedConverters;
  private final com.mageddo.dnsproxyserver.config.mapper.ConfigMapper configMapper;

  public Config find() {
    final var converters = this.findConvertersSorted();
    final var configs = this.findConfigs(converters);
    return this.configMapper.mapFrom(configs);
  }

  private List<Config> findConfigs(List<Converter> converters) {
    return converters
      .stream()
      .map(Converter::parse)
      .map(ConfigMapper::of)
      .toList();
  }

  List<Converter> findConvertersSorted() {
    throw new UnsupportedOperationException();
  }

}
