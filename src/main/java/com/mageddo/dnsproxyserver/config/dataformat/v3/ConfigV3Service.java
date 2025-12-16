package com.mageddo.dnsproxyserver.config.dataformat.v3;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigMapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigV3Service {

  private final List<ConfigDAO> unorderedConfigDAOS;
  private final com.mageddo.dnsproxyserver.config.mapper.ConfigMapper configMapper;

  public Config find() {
    final var converters = this.findConvertersSorted();
    final var configs = this.findConfigs(converters);
    return this.configMapper.mapFrom(configs);
  }

  private List<Config> findConfigs(List<ConfigDAO> configDAOS) {
    return configDAOS
      .stream()
      .map(ConfigDAO::find)
      .map(ConfigMapper::of)
      .toList();
  }

  List<ConfigDAO> findConvertersSorted() {
    throw new UnsupportedOperationException();
  }

}
