package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.ConfigFileFinderService;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigJsonV2Mapper;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.utils.Tests;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
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

  static boolean runningInTestsAndNoCustomConfigPath(Path configPath) {
    return isDefaultConfigFilePath(configPath) && Tests.inTest();
  }

  private static boolean isDefaultConfigFilePath(Path configPath) {
    return ConfigFlag.DEFAULT_CONFIG_FILE_AS_PATH.equals(configPath);
  }

  @Override
  public int priority() {
    return 2;
  }
}
