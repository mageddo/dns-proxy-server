package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.di.Context;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DaggerTest(component = Context.class)
class ConfigServiceCompTest {

  static final String[] excludingFields = new String[]{
    "version", "configPath", "resolvConfPaths",
    "dockerHost"
  };

  @Inject
  ConfigService service;

  @Test
  void mustPutDaosInTheExpectedOrder() {
    // arrange

    // act
    final var names = this.service.findConfigNames();

    // assert
    assertEquals("[ConfigDAOEnv, ConfigDAOJson, ConfigDAOCmdArgs]", names.toString());
  }

  @Test
  void mustParseDefaultConfigsAndCreateJsonConfigFile(@TempDir Path tmpDir) {

    // arrange
    final var jsonConfigFile = tmpDir.resolve("tmpfile.json");
    ConfigDAOCmdArgs.setArgs(new String[]{"--conf-path", jsonConfigFile.toString()});
    assertFalse(Files.exists(jsonConfigFile));

    // act
    final var config = Configs.getContext()
      .configService()
      .findCurrentConfig()
      ;

    // assert
    assertParsedConfig(config);
    assertWrittenFile(jsonConfigFile);
  }

  static void assertParsedConfig(Config config) {
    assertEquals(
      readAndSortJsonExcluding("/configs-test/001.json", excludingFields),
      readAndSortJsonExcluding(config, excludingFields)
    );
  }

  static void assertWrittenFile(Path jsonConfigFile) {
    assertTrue(Files.exists(jsonConfigFile));
    assertEquals(readAndSortJson("/configs-test/002.json"), readAndSortJson(jsonConfigFile));
  }

  @Test
  void mustParseLowerCaseLogLevel(){
    // arrange
    ConfigDAOCmdArgs.setArgs(new String[]{"--log-level", "warning"});

    // act
    final var config = Configs.getContext()
      .configService()
      .findCurrentConfig()
      ;

    // assert
    assertEquals(LogLevel.WARNING, config.getLogLevel());
  }
}
