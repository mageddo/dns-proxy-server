package com.mageddo.dnsproxyserver.config.entrypoint;

import com.mageddo.dnsproxyserver.config.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2.CanaryRateThresholdCircuitBreaker;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJsonV2.StaticThresholdCircuitBreaker;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static com.mageddo.dnsproxyserver.config.dataprovider.JsonConfigs.findVersion;
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

  @Test
  void mustParseDefaultCircuitBreakerStrategyAsStaticThreshold(){

    final var json = """
      {
        "version": 2,
        "solverRemote" : {
          "circuitBreaker" : {
            "failureThreshold": 3,
            "failureThresholdCapacity": 5,
            "successThreshold": 10,
            "testDelay": "PT20S"
          }
        }
      }
      """;

    final var config = JsonConfigs.loadConfig(json);

    assertNotNull(config);
    assertStaticThresholdCircuitBreakerConfig((StaticThresholdCircuitBreaker) config.getSolverRemoteCircuitBreaker());
  }

  @Test
  void mustParseCanaryRateThresholdCircuitBreakerStrategy(){

    final var json = """
      {
        "version": 2,
        "solverRemote" : {
          "circuitBreaker" : {
            "strategy": "CANARY_RATE_THRESHOLD",
            "failureRateThreshold" : 21.9,
            "minimumNumberOfCalls" : 50,
            "permittedNumberOfCallsInHalfOpenState" : 10
          }
        }
      }
      """;

    final var config = JsonConfigs.loadConfig(json);
    assertNotNull(config);

    final var circuitBreaker = (CanaryRateThresholdCircuitBreaker) config.getSolverRemoteCircuitBreaker();
    assertEquals(21.9f, circuitBreaker.getFailureRateThreshold(), 1);
    assertEquals(50, circuitBreaker.getMinimumNumberOfCalls());
    assertEquals(10, circuitBreaker.getPermittedNumberOfCallsInHalfOpenState());

  }

  void assertStaticThresholdCircuitBreakerConfig(StaticThresholdCircuitBreaker circuitBreaker) {
    assertEquals(3, circuitBreaker.getFailureThreshold());
    assertEquals(5, circuitBreaker.getFailureThresholdCapacity());
    assertEquals(10, circuitBreaker.getSuccessThreshold());
    assertEquals(Duration.ofSeconds(20), circuitBreaker.getTestDelay());
  }
}
