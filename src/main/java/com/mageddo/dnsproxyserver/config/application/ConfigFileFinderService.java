package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.ConfigDAOCmdArgs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv.ConfigDAOLegacyEnv;
import com.mageddo.dnsproxyserver.config.dataformat.v2.legacyenv.ConfigEnv;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.vo.ConfigFlag;
import com.mageddo.dnsproxyserver.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigFileFinderService {

  private final ConfigDAOLegacyEnv configDAOEnv;
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
