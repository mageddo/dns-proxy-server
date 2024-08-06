package com.mageddo.dnsproxyserver.config.dataprovider;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.ConfigFlagTemplates;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.sortJsonExcluding;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class ConfigDAOJsonTest {

  static final String[] excludingFields = new String[]{
    "version", "configPath", "resolvConfPaths",
    "dockerHost"
  };

  final ConfigDAOJson configDAOJson = new ConfigDAOJson(null, null);

  @Test
  void mustBuildConfPathRelativeToWorkDir(@TempDir Path tmpDir){
    // arrange
    final var workDir = tmpDir.resolve("custom-work-dir");
    final var flags = ConfigFlagTemplates.defaultWithConfigPath(Paths.get("conf/config.json"));
    ConfigDAOCmdArgs.setArgs(flags.getArgs());

    // act
    final var configPath = ConfigPathBuilder.build(workDir, flags.getConfigPath());

    // assert
    assertEquals("config.json", configPath.getFileName().toString());
    assertEquals(workDir.getFileName().toString(), configPath.getParent().getParent().getFileName().toString());
  }

  @Test
  void mustReadAndRespectStoredConfigFile(@TempDir Path tmpDir) {
    // arrange
    final var sourceConfigFile = "/configs-test/003.json";
    final var configPathToUse = tmpDir.resolve("tmpfile.json");
    writeCurrentConfigFile(sourceConfigFile, configPathToUse);

    // act
    final var config = this.configDAOJson.find(configPathToUse);

    // assert
    assertEquals(
      readAndSortJsonExcluding("/configs-test/004.json", excludingFields),
      sortJsonExcluding(config, excludingFields)
    );
  }

  @Test
  void mustDisableRemoteServersRespectingConfig(@TempDir Path tmpDir) {
    // arrange
    final var sourceConfigFile = "/configs-test/005.json";
    final var configPathToUse = tmpDir.resolve("tmpfile.json");
    writeCurrentConfigFile(sourceConfigFile, configPathToUse);

    // act
    final var config = this.configDAOJson.find(configPathToUse);

    // assert
    assertFalse(config.isSolverRemoteActive());
  }

  @SneakyThrows
  static void writeCurrentConfigFile(String sourceResource, Path target) {
    try (var out = Files.newOutputStream(target)) {
      IOUtils.copy(readAsStream(sourceResource), out);
    }
  }


}
