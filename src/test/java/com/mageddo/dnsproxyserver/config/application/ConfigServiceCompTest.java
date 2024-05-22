package com.mageddo.dnsproxyserver.config.application;

import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.configurator.Context;
import com.mageddo.dnsproxyserver.config.dataprovider.ConfigDAOCmdArgs;
import dagger.sheath.junit.DaggerTest;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.sortJsonExcluding;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testing.JsonAssertion.jsonPath;

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
  void mustParseDefaultConfigsAndCreateConfigFile(@TempDir Path tmpDir) {

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
    assertEquals(
      readAndSortJsonExcluding("/configs-test/001.json", excludingFields),
      readAndSortJsonExcluding(config, excludingFields)
    );
    assertTrue(Files.exists(jsonConfigFile));
    assertEquals(readAndSortJson("/configs-test/002.json"), readAndSortJson(jsonConfigFile));
  }


  @Test
  @SneakyThrows
  void mustRespectStoredConfig(@TempDir Path tmpDir) {

    // arrange
    final var jsonConfigFile = "/configs-test/003.json";
    final var tmpConfigFile = tmpDir.resolve("tmpfile.json");

    try (var out = Files.newOutputStream(tmpConfigFile)) {
      IOUtils.copy(readAsStream(jsonConfigFile), out);
    }
    assertTrue(Files.exists(tmpConfigFile));

    ConfigDAOCmdArgs.setArgs(new String[]{"--conf-path", tmpConfigFile.toString()});

    // act
    final var config = Configs.getContext()
      .configService()
      .findCurrentConfig()
      ;

    // assert
    assertEquals(
      readAndSortJsonExcluding("/configs-test/004.json", excludingFields),
      sortJsonExcluding(config, excludingFields)
    );
    assertThat(
      jsonPath(config).getString("dockerHost"),
      anyOf(containsString("unix:"), containsString("npipe"))
    );

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
