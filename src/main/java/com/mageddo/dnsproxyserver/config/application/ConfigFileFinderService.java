package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOEnv;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigEnv;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ConfigFileFinderService {

  private final ConfigDAOEnv configDAOEnv;
  private final ConfigDAOCmdArgs configDAOCmdArgs;

  public Path findPath(){
    final var envConfig = this.configDAOEnv.findRaw();
    final var argsConfig = this.configDAOCmdArgs.findRaw();

    final var configFilePath = this.findConfigFilePath(envConfig, argsConfig);

    final var workDir = envConfig.getWorkingDir();
    return ConfigPathMapper.build(workDir, configFilePath);
  }

  private Path findConfigFilePath(ConfigEnv envConfig, ConfigFlag argsConfig) {
    return ObjectUtils.firstNonNullRequiring(
      envConfig.getConfigFilePath(),
      argsConfig.getConfigFileAsPath()
    );
  }
}
