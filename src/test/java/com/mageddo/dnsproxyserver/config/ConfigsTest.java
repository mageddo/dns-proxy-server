package com.mageddo.dnsproxyserver.config;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.readString;
import static com.mageddo.utils.TestUtils.sortJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigsTest {

  @Test
  void mustParseDefaultConfigsAndCreateConfigFile(@TempDir Path tmpDir) {

    // arrange
    final var tmpConfigFile = tmpDir.resolve("tmpfile.json");
    final var args = new String[]{"--conf-path", tmpConfigFile.toString()};
    assertFalse(Files.exists(tmpConfigFile));

    // act
    final var config = Configs.buildAndRegister(args);

    // assert
    final var expectedJsonConfig = readAndSortJson("/configs-test/001.json");
    assertEquals(expectedJsonConfig, sortJson(config));
    assertTrue(Files.exists(tmpConfigFile));

    assertEquals(expectedJsonConfig, sortJson(config));
    assertEquals(sortJson(readString("/configs-test/002.json")), sortJson(readString(tmpConfigFile)));
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

    final var args = new String[]{"--conf-path", tmpConfigFile.toString()};

    // act
    final var config = Configs.buildAndRegister(args);

    // assert
    final var expectedJsonConfig = readAndSortJson("/configs-test/004.json");
    assertEquals(expectedJsonConfig, sortJson(config));
  }

}