package com.mageddo.dnsproxyserver.config.entrypoint;

import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.dnsproxyserver.config.entrypoint.JsonConfigs.findVersion;
import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAsStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonConfigsTest {

  @Test
  void mustParseVersion1ConvertAndSaveAsVersion2WhenChanged(@TempDir Path tempDir) throws Exception {
    // arrange
    final var tempJsonConfigPath = tempDir.resolve("config.tmp.json");
    Files.copy(readAsStream("/config-json-v1-test/001.json"), tempJsonConfigPath);

    // act
    // assert

    final var configJson = JsonConfigs.loadConfig(tempJsonConfigPath);
    assertTrue(configJson instanceof ConfigJsonV2, ClassUtils.getSimpleName(configJson));
    assertEquals(JsonConfigs.VERSION_1, findVersion(tempJsonConfigPath));

    JsonConfigs.write(tempJsonConfigPath, (ConfigJsonV2) configJson);
    assertEquals(JsonConfigs.VERSION_2, findVersion(tempJsonConfigPath));

    final var path = JsonConfigs.buildBackupPath(tempJsonConfigPath);
    assertTrue(Files.exists(path), path.toString());

    assertEquals(readAndSortJson("/json-configs-test/001.json"), readAndSortJson(tempJsonConfigPath));
  }

  @Test
  void mustCreateDefaultConfigJsonFileVersion2WhenItDoesntExists(@TempDir Path tempDir){

    // arrange
    final var tempConfig = tempDir.resolve("config.tmp.json");

    // act
    final var configJson = JsonConfigs.loadConfig(tempConfig);

    // assert
    assertTrue(configJson instanceof ConfigJsonV2, ClassUtils.getSimpleName(configJson));
    assertEquals(JsonConfigs.VERSION_2, findVersion(tempConfig));

  }

  @Test
  void mustCreateDefaultConfigFileEvenWhenDirectoryDoesntExists(@TempDir Path tempDir){
    // arrange
    final var tempConfig = tempDir.resolve("some-random-dir").resolve("config.tmp.json");

    // act
    final var configJson = JsonConfigs.loadConfig(tempConfig);

    // assert
    assertNotNull(configJson);
    assertTrue(Files.exists(tempConfig));
  }

}
