package com.mageddo.dnsproxyserver.config.application;

import java.nio.file.Files;
import java.nio.file.Path;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.configurer.di.Context;
import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.ConfigDAOCmdArgs;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dagger.sheath.junit.DaggerTest;
import lombok.SneakyThrows;

import static com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig.Type.CANARY_RATE_THRESHOLD;
import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.readSortDonWriteNullsAndExcludeFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DaggerTest(component = Context.class)
class ConfigV2ServiceCompTest {

  static final String[] excludingFields = new String[]{
      "version", "configPath", "resolvConfPaths",
      "dockerHost"
  };

  @Test
  void mustParseDefaultConfigsAndNotCreateJsonConfigFileUntilItIsChanged(
      @TempDir Path tmpDir
  ) {

    // arrange
    final var jsonConfigFile = tmpDir.resolve("tmpfile.json");
    ConfigDAOCmdArgs.setArgs(new String[]{"--conf-path", jsonConfigFile.toString()});
    assertFalse(Files.exists(jsonConfigFile));

    // act
    final var config = Configs.getContext()
        .configService()
        .find();

    // assert
    assertFalse(config.getRegisterContainerNames());
    assertFalse(Files.exists(jsonConfigFile));
  }


  @Test
  void mustParseLowerCaseLogLevel() {
    // arrange
    ConfigDAOCmdArgs.setArgs(new String[]{"--log-level", "warning"});

    // act
    final var config = Configs.getContext()
        .configService()
        .find();

    // assert
    assertEquals(Config.Log.Level.WARNING, config.getLogLevel());
  }

  @Test
  void mustDisableRemoteServersRespectingConfig(@TempDir Path tmpDir) {
    // arrange
    writeAndSetCustomConfigFile(tmpDir, "/configs-test/006.json");

    // act
    final var config = Configs.getContext()
        .configService()
        .find();

    // assert
    assertFalse(config.isSolverRemoteActive());

  }

  @Test
  void mustParseCanaryRateThreshold(@TempDir Path tmpDir) {
    // arrange
    writeAndSetCustomConfigFile(tmpDir, "/configs-test/009.json");

    // act
    final var config = Configs.getContext()
        .configService()
        .find();

    // assert
    assertEquals(CANARY_RATE_THRESHOLD, config.getSolverRemoteCircuitBreakerStrategy()
        .getType()
    );
  }

  static void assertParsedConfig(Config config, String expectedFilePath) {
    assertEquals(
        readAndSortJsonExcluding(expectedFilePath, excludingFields),
        readAndSortJsonExcluding(config, excludingFields)
    );
  }

  static void assertWrittenFile(String expectedFilePath, Path jsonConfigFile) {
    assertTrue(Files.exists(jsonConfigFile));
    assertEquals(readAndSortJson(expectedFilePath),
        readSortDonWriteNullsAndExcludeFields(jsonConfigFile)
    );
  }

  static void writeAndSetCustomConfigFile(Path tmpDir, String sourceConfigFile) {
    final var configPathToUse = tmpDir.resolve("tmpfile.json");
    writeCurrentConfigFile(sourceConfigFile, configPathToUse);
    ConfigDAOCmdArgs.setArgs(new String[]{"--conf-path", configPathToUse.toString()});
  }

  @SneakyThrows
  static void writeCurrentConfigFile(String sourceResource, Path target) {
    try (var out = Files.newOutputStream(target)) {
      IOUtils.copy(readAsStream(sourceResource), out);
    }
  }
}
