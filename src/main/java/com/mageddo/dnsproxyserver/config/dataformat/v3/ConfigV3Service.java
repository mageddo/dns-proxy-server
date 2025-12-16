package com.mageddo.dnsproxyserver.config.dataformat.v3;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

@Singleton
public class ConfigV3Service {

  private final List<ConfigDAO> daos;
  private final ConfigMapper configMapper;

  @Inject
  public ConfigV3Service(Instance<ConfigDAO> daos, ConfigMapper configMapper) {
    this.daos = daos
        .stream()
        .sorted(Comparator.comparing(ConfigDAO::priority))
        .toList()
    ;
    this.configMapper = configMapper;
  }

  public Config find() {
    final var configs = this.daos.stream()
        .map(ConfigDAO::find)
        .filter(Objects::nonNull)
        .toList();
    return this.configMapper.mapFrom(configs);
  }

}
