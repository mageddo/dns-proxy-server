package com.mageddo.dnsproxyserver.config.provider.dataformatv3;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.mapper.ConfigV3Mapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter.Converter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigParseService {

  private final List<Converter> unorderedConverters;
  private final ConfigMapper configMapper;

  public Config parseMerging(){
    final var parsers = this.findParsersInOrder();
    final var configs = this.findConfigs(parsers);
    return this.configMapper.mapFrom(configs);
  }

  private List<Config> findConfigs(List<Converter> converters) {
    return converters.stream()
                     .map(Converter::parse)
                     .map(ConfigV3Mapper::toConfig)
                     .toList();
  }

  public List<Converter> findParsersInOrder() {
    throw new UnsupportedOperationException();
  }

}
