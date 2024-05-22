package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAO;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;
import org.apache.commons.lang3.ClassUtils;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class ConfigService {

  private final List<ConfigDAO> configDAOS;

  @Inject
  public ConfigService(Instance<ConfigDAO> configDAOS) {
    this.configDAOS = configDAOS
      .stream()
      .toList()
    ;
  }

  public Config findCurrentConfig() {
    return ConfigMapper.mapFrom(this.findConfigs());
  }

  List<Config> findConfigs() {
    return this.findConfigDaos()
      .map(ConfigDAO::find)
      .toList();
  }

  Stream<ConfigDAO> findConfigDaos() {
    return this.configDAOS
      .stream()
      .sorted(Comparator.comparingInt(ConfigDAO::priority));
  }

  public List<String> findConfigNames(){
    return this.findConfigDaos()
      .map(ClassUtils::getSimpleName)
      .toList();
  }
}
