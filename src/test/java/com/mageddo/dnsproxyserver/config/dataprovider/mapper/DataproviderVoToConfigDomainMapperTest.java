package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.LogLevel;
import com.mageddo.dnsproxyserver.config.dataprovider.MultiSourceConfigDAOCmdArgs;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testing.templates.ConfigFlagTemplates;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mageddo.utils.TestUtils.readAndSortJson;
import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.sortJsonExcluding;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static testing.JsonAssertion.jsonPath;

class DataproviderVoToConfigDomainMapperTest {

  static final String[] excludingFields = new String[]{
    "version", "configPath", "resolvConfPaths",
    "dockerHost"
  };

  @Test
  void mustParseDefaultConfigsAndCreateConfigFile(@TempDir Path tmpDir) {

    // arrange
    final var jsonConfigFile = tmpDir.resolve("tmpfile.json");
    final var args = new String[]{"--conf-path", jsonConfigFile.toString()};
    assertFalse(Files.exists(jsonConfigFile));

    // act
    final var config = MultiSourceConfigDAOCmdArgs.build(args);

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

    final var args = new String[]{"--conf-path", tmpConfigFile.toString()};

    // act
    final var config = MultiSourceConfigDAOCmdArgs.build(args);

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
  void mustBuildConfPathRelativeToWorkDir(@TempDir Path tmpDir){
    // arrange
    final var flags = ConfigFlagTemplates.defaultWithConfigPath(Paths.get("conf/config.json"));
    final var workDir = tmpDir.resolve("custom-work-dir");

    // act
    final var configPath = DataproviderVoToConfigDomainMapper.buildConfigPath(flags, workDir);

    // assert
    assertEquals("config.json", configPath.getFileName().toString());
    assertEquals(workDir.getFileName().toString(), configPath.getParent().getParent().getFileName().toString());
  }

  @Test
  void mustParseLowerCaseLogLevel(){
    // arrange
    final var args = new String[]{"--log-level", "warning"};

    // act
    final var config = MultiSourceConfigDAOCmdArgs.build(args);

    // assert
    assertEquals(LogLevel.WARNING, config.getLogLevel());
  }
}
