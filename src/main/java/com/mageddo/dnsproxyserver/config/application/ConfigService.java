package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAO;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;

@Singleton
public class ConfigService {

  private final List<MultiSourceConfigDAO> configDAOS;

  @Inject
  public ConfigService(Instance<MultiSourceConfigDAO> configDAOS) {
    this.configDAOS = configDAOS
      .stream()
      .toList()
    ;
  }

  public Config findCurrentConfig() {
    return ConfigMapper.mapFrom(this.findConfigs());
  }

  List<Config> findConfigs() {
    return this.configDAOS
      .stream()
      .sorted(Comparator.comparingInt(MultiSourceConfigDAO::priority))
      .map(MultiSourceConfigDAO::find)
      .toList();
  }
}
