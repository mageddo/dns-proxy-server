package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.templates.ConfigFlagTemplates;
import com.mageddo.dnsproxyserver.templates.EnvTemplates;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.readString;
import static com.mageddo.utils.TestUtils.sortJson;
import static com.mageddo.utils.TestUtils.sortJsonExcluding;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigsTest {

  static final String[] excludingFields = new String[]{"version", "configPath"};

  @Test
  void mustParseDefaultConfigsAndCreateConfigFile(@TempDir Path tmpDir) {

    // arrange
    final var tmpConfigFile = tmpDir.resolve("tmpfile.json");
    final var args = new String[]{"--conf-path", tmpConfigFile.toString()};
    assertFalse(Files.exists(tmpConfigFile));

    // act
    final var config = Configs.buildAndRegister(args);

    // assert
    assertEquals(
      readAndSortJsonExcluding("/configs-test/001.json", excludingFields),
      readAndSortJsonExcluding(config, excludingFields)
    );
    assertTrue(Files.exists(tmpConfigFile));

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
    assertEquals(
      readAndSortJsonExcluding("/configs-test/004.json", excludingFields),
      sortJsonExcluding(config, excludingFields)
    );
  }

  @Test
  void mustGenerateEnvHostnameIdWhenIsNull() {
    // arrange

    // act
    final var env = EnvTemplates.buildWithoutId();
    final var firstEntry = env
      .getEntries()
      .stream()
      .findFirst()
      .get();

    // assert
    assertNotNull(firstEntry.getId());
    final var currentNanoTime = System.nanoTime();
    assertTrue(
      firstEntry.getId() < currentNanoTime,
      String.format("id=%s, currentTimeInMillis=%s", firstEntry.getId(), currentNanoTime)
    );
  }

  @Test
  void mustBuildConfPathRelativeToWorkDir(){
    // arrange
    final var flags = ConfigFlagTemplates.defaultWithConfigPath(Paths.get("conf/config.json"));
    final var currentPath = Paths.get("/tmp/");

    // act
    final var path = Configs.buildConfigPath(flags, currentPath);

    // assert
    assertEquals("/tmp/conf/config.json", path.toString());
  }

  @Test
  void mustBuildConfPathRelativeToBinaryRunningPathWhenWorkDirIsNotSet(){
    // arrange
    final var flags = ConfigFlagTemplates.defaultWithConfigPath(Paths.get("conf/config.json"));
    final Path currentPath = null;

    // act
    final var path = Configs.buildConfigPath(flags, currentPath);

    // assert
    assertEquals("/home/typer/dev/projects/dns-proxy-server/conf/config.json", path.toString());


  }
}
