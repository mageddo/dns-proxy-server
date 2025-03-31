package com.mageddo.dnsproxyserver.config.provider.jsonv1v2.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.provider.jsonv1v2.vo.ConfigJson;
import org.junit.jupiter.api.Test;
import testing.templates.ConfigJsonTemplates;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig.Name.CANARY_RATE_THRESHOLD;
import static org.junit.jupiter.api.Assertions.*;

class ConfigJsonV2MapperTest {

  static final Path RANDOM_CONFIG_PATH = Paths.get("/tmp/conf.json");

  @Test
  void mustMapSolverRemoteAsInactiveWhenNoRemoteServersFlagIsSet(){
    // arrange
    final var configJson = ConfigJsonTemplates.withNoRemoteServersAndCircuitBreakerDefined();

    // act
    final var config = toConfig(configJson);

    // assert
    assertFalse(config.isSolverRemoteActive());
  }

  @Test
  void mustMapSolverRemoteAsInactiveEvenWhenCircuitBreakerIsNOTSet(){
    // arrange
    final var configJson = ConfigJsonTemplates.withoutCircuitBreakerDefinedWithNoRemoteServers();

    // act
    final var config = toConfig(configJson);

    // assert
    assertFalse(config.isSolverRemoteActive());
  }

  @Test
  void mustReturnNullWhenNothingIsSet(){
    // arrange
    final var configJson = ConfigJsonTemplates.noRemoteServerFlagsSet();

    // act
    final var config = toConfig(configJson);

    // assert
    assertNull(config.getSolverRemote());
  }

  @Test
  void mustMapCanaryRateCircuitBreaker(){
    final var configJson = ConfigJsonTemplates.canaryRateThresholdCircuitBreaker();

    final var config = toConfig(configJson);

    final var circuitBreakerStrategyConfig = config.getSolverRemoteCircuitBreakerStrategy();
    assertEquals(CANARY_RATE_THRESHOLD, circuitBreakerStrategyConfig.name());

  }

  @Test
  void mustMapRemoteDnsServerAddress(){
    final var configJson = ConfigJsonTemplates.withDnsServers();

    final var config = toConfig(configJson);

    final var dnsServers = config.getRemoteDnsServers();
    assertNotNull(dnsServers);
    assertEquals(1, dnsServers.size());
    assertEquals("4.4.4.4", String.valueOf(dnsServers.getFirst()));
  }

  static Config toConfig(ConfigJson configJson) {
    return ConfigJsonV2Mapper.toConfig(configJson, RANDOM_CONFIG_PATH);
  }
}
