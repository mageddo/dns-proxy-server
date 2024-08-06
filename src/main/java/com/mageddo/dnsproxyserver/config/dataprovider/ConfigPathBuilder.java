package com.mageddo.dnsproxyserver.config.dataprovider;

import com.mageddo.utils.Files;
import com.mageddo.utils.Runtime;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ConfigPathBuilder {
  private static Path build0(Path workDir, String configPath) {
    if (ConfigDAOJson.runningInTestsAndNoCustomConfigPath()) {
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
    final var confRelativeToCurrDir = Paths
      .get(configPath)
      .toAbsolutePath();
    if (Files.exists(confRelativeToCurrDir)) {
      return confRelativeToCurrDir;
    }
    return Runtime.getRunningDir()
      .resolve(configPath)
      .toAbsolutePath();
  }

  public static Path build(Path workDir, String relativeConfigFilePath) {
    final var path = build0(workDir, relativeConfigFilePath);
    log.debug("status=configPathBuilt, path={}", path);
    return path;
  }
}
