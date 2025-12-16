package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.ConfigFileFinderService;
import com.mageddo.dnsproxyserver.config.dataformat.v2.ConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.mapper.ConfigJsonV2Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOJson implements ConfigDAO {

  private final ConfigFileFinderService configFileFinderService;

  @Override
  public Config find() {
    return this.find(this.configFileFinderService.findPath());
  }

  public Config find(Path configPath) {
    final var jsonConfig = JsonConfigs.loadConfig(configPath);
    log.debug("configPath={}", configPath);
    return ConfigJsonV2Mapper.toConfig(jsonConfig, configPath);
  }

  @Override
  public int priority() {
    return 2;
  }
}
