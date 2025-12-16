package com.mageddo.dnsproxyserver.config.dataformat.v2;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.SolverRemote;
import com.mageddo.dnsproxyserver.config.mapper.ConfigMapper;

import org.apache.commons.lang3.ClassUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ConfigV2Service {

  private final List<ConfigDAO> configDAOS;
  private final ConfigMapper configMapper;

  @Inject
  public ConfigV2Service(Instance<ConfigDAO> configDAOS, ConfigMapper configMapper) {
    this.configDAOS = configDAOS
        .stream()
        .toList()
    ;
    this.configMapper = configMapper;
  }

  public Config findCurrentConfig() {
    final var configs = this.findConfigs();
    log.trace("baseConfigs={}", configs);
    final var config = this.configMapper.mapFrom(configs);
    return config;
  }

  List<Config> findConfigs() {
    final var config = this.findConfigDaos()
        .map(ConfigDAO::find)
        .toList();
    return config;
  }

  Stream<ConfigDAO> findConfigDaos() {
    return this.configDAOS
        .stream()
        .sorted(Comparator.comparingInt(ConfigDAO::priority));
  }

  List<String> findConfigNames() {
    return this.findConfigDaos()
        .map(ClassUtils::getSimpleName)
        .toList();
  }

  SolverRemote findCurrentConfigRemote() {
    return this.findCurrentConfig()
        .getSolverRemote()
        ;
  }

  public CircuitBreakerStrategyConfig findCurrentConfigCircuitBreaker() {
    return this.findCurrentConfigRemote()
        .getCircuitBreaker();
  }
}
