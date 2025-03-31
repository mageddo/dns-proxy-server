package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.provider.cmdargs.ConfigDAOCmdArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testing.templates.ConfigFlagTemplates;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPathMapperTest {

  @Test
  void mustBuildConfPathRelativeToWorkDir(@TempDir Path tmpDir){
    // arrange
    final var workDir = tmpDir.resolve("custom-work-dir");
    final var flags = ConfigFlagTemplates.defaultWithConfigPath(Paths.get("conf/config-custom.json"));
    ConfigDAOCmdArgs.setArgs(flags.getArgs());

    // act
    final var configPath = ConfigPathMapper.build(workDir, flags.getConfigFileAsPath());

    // assert
    assertEquals("config-custom.json", configPath.getFileName().toString());
    assertEquals(workDir.getFileName().toString(), configPath.getParent().getParent().getFileName().toString());
  }

}
