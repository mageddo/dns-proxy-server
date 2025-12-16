package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.vo.ConfigFlag;
import com.mageddo.utils.Files;
import com.mageddo.utils.Runtime;
import com.mageddo.utils.Tests;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
class ConfigPathMapper {

  public static Path build(Path workDir, Path configFilePath) {
    final var path = build0(workDir, configFilePath);
    log.debug("status=configPathBuilt, path={}", path);
    return path;
  }

  private static Path build0(Path workDir, Path configPath) {
    if (runningInTestsAndNoCustomConfigPath(configPath)) {
      final var file = Files.createTempFileDeleteOnExit("dns-proxy-server-junit", ".json");
      log.trace("status=runningInTests, usingEmptyFile={}", file);
      return file;
    }
    if (workDir != null) {
      return workDir
        .resolve(configPath)
        .toAbsolutePath()
        ;
    }
    final var confRelativeToCurrDir = configPath.toAbsolutePath();
    if (Files.exists(confRelativeToCurrDir)) {
      return confRelativeToCurrDir;
    }
    return Runtime.getRunningDir()
      .resolve(configPath)
      .toAbsolutePath();
  }

  private static boolean runningInTestsAndNoCustomConfigPath(Path configPath) {
    return isDefaultConfigFilePath(configPath) && Tests.inTest();
  }

  private static boolean isDefaultConfigFilePath(Path configPath) {
    return ConfigFlag.DEFAULT_CONFIG_FILE_AS_PATH.equals(configPath);
  }
}
