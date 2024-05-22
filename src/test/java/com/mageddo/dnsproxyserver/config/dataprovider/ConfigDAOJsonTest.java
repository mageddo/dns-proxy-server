package com.mageddo.dnsproxyserver.config.dataprovider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testing.templates.ConfigFlagTemplates;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigDAOJsonTest {

  @Test
  void mustBuildConfPathRelativeToWorkDir(@TempDir Path tmpDir){
    // arrange
    final var workDir = tmpDir.resolve("custom-work-dir");
    final var flags = ConfigFlagTemplates.defaultWithConfigPath(Paths.get("conf/config.json"));
    ConfigDAOCmdArgs.setArgs(flags.getArgs());

    // act
    final var configPath = ConfigDAOJson.buildConfigPath(workDir, flags.getConfigPath());

    // assert
    assertEquals("config.json", configPath.getFileName().toString());
    assertEquals(workDir.getFileName().toString(), configPath.getParent().getParent().getFileName().toString());
  }



}
