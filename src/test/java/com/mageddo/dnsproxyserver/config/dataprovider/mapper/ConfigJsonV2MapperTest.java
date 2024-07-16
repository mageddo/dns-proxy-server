package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.vo.ConfigJson;
import org.junit.jupiter.api.Test;
import testing.templates.ConfigJsonTemplates;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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

  static Config toConfig(ConfigJson configJson) {
    return ConfigJsonV2Mapper.toConfig(configJson, RANDOM_CONFIG_PATH);
  }
}
