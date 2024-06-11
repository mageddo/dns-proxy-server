package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.ConfigFlagTemplates;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigFlagMapperTest {

  @Test
  void mustSetHelpFlag() {
    // arrange
    final var configFlag = ConfigFlagTemplates.withHelpFlag();

    // act
    final var config = ConfigFlagMapper.toConfig(configFlag);

    // assert
    assertTrue(config.isHelpCmd());
  }


  @Test
  void mustSetVersionFlag() {
    // arrange
    final var configFlag = ConfigFlagTemplates.withVersionFlag();

    // act
    final var config = ConfigFlagMapper.toConfig(configFlag);

    // assert
    assertTrue(config.isVersionCmd());
  }

}
