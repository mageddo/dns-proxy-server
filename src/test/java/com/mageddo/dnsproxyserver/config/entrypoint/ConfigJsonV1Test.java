package com.mageddo.dnsproxyserver.config.entrypoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.sortJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigJsonV1Test {

  @Test
  void mustParseV1Config(@TempDir Path tmpDir) throws Exception {
    // arrange
    final var configV1 = tmpDir.resolve("config-v1.json");
    Files.copy(readAsStream("/config-json-v1-test/001.json"), configV1);

    // act
    final var config = JsonConfigs.loadConfig(configV1);

    // assert
    assertEquals(readAndSortJson("/config-json-v1-test/002.json"), sortJson(config));
  }

}
