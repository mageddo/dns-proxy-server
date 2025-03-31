package com.mageddo.dnsproxyserver.config.provider.dataformatv3;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.mapper.ConfigV3Mapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser.Parser;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigParseService {

  private final List<Parser> unorderedParsers;
  private final ConfigMapper configMapper;

  public Config parseMerging(){
    final var parsers = this.findParsersInOrder();
    final var configs = this.findConfigs(parsers);
    return this.configMapper.mapFrom(configs);
  }

  private List<Config> findConfigs(List<Parser> parsers) {
    return parsers.stream()
           .map(Parser::parse)
           .map(ConfigV3Mapper::toConfig)
           .toList();
  }

  public List<Parser> findParsersInOrder() {
    throw new UnsupportedOperationException();
  }

}
