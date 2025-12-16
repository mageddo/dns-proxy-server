package com.mageddo.dnsproxyserver.config.provider.jsonv1v2.dataprovider;

import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.ConfigDAOJson;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.mageddo.utils.TestUtils.readAndSortJsonExcluding;
import static com.mageddo.utils.TestUtils.readAsStream;
import static com.mageddo.utils.TestUtils.readSortDonWriteNullsAndExcludeFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ConfigDAOJsonTest {

  static final String[] excludingFields = new String[]{
    "version", "configPath", "resolvConfPaths",
    "dockerHost"
  };

  final ConfigDAOJson configDAOJson = new ConfigDAOJson(null);

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
      readSortDonWriteNullsAndExcludeFields(config, excludingFields)
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

  @Test
  void mustConfigureStubSolverDomain(@TempDir Path tmpDir){
    // arrange
    final var sourceConfigFile = "/configs-test/010.json";
    final var configPathToUse = tmpDir.resolve("tmpfile.json");
    writeCurrentConfigFile(sourceConfigFile, configPathToUse);

    // act
    final var config = this.configDAOJson.find(configPathToUse);

    // assert
    final var solverStub = config.getSolverStub();
    assertNotNull(solverStub);
    assertEquals("acme", solverStub.getDomainName());
  }

  @SneakyThrows
  static void writeCurrentConfigFile(String sourceResource, Path target) {
    try (var out = Files.newOutputStream(target)) {
      IOUtils.copy(readAsStream(sourceResource), out);
    }
  }


}
