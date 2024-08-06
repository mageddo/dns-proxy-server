package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigJsonV2Mapper;
import com.mageddo.utils.Tests;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigDAOJson implements ConfigDAO {

  private final ConfigDAOEnv configDAOEnv;
  private final ConfigDAOCmdArgs configDAOCmdArgs;

  @Override
  public Config find() {
    final var workDir = this.configDAOEnv.findRaw().getCurrentPath();
    final var relativeConfigFilePath = this.configDAOCmdArgs.findRaw().getConfigPath();
    final var configFileAbsolutePath = ConfigPathBuilder.build(workDir, relativeConfigFilePath);
    return this.find(configFileAbsolutePath);
  }

  public Config find(Path configPath) {
    final var jsonConfig = JsonConfigs.loadConfig(configPath);
    log.debug("configPath={}", configPath);
    return ConfigJsonV2Mapper.toConfig(jsonConfig, configPath);
  }

  static boolean runningInTestsAndNoCustomConfigPath() {
    return !Arrays.toString(ConfigDAOCmdArgs.getArgs()).contains("--conf-path") && Tests.inTest();
  }

  @Override
  public int priority() {
    return 2;
  }
}
