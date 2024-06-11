package com.mageddo.dnsproxyserver.config.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.ConfigTemplates;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConfigMapperTest {

  @Test
  void mustMapFromDaoConfigsToCurrentConfig() {
    // arrange
    final var config = ConfigTemplates.defaultWithoutId();

    // act
    final var currentConfig = ConfigMapper.mapFrom(List.of(config));

    // assert
    assertNotNull(currentConfig);
  }

  @Test
  void mustMapIsVersionCmd() {
    // arrange
    final var config = ConfigTemplates.withVersionCmd();

    // act
    final var currentConfig = ConfigMapper.mapFrom(List.of(config));

    // assert
    assertNotNull(currentConfig);
    assertTrue(currentConfig.isVersionCmd());
  }

  @Test
  void mustMapIsHelpCmd() {
    // arrange
    final var config = ConfigTemplates.withHelpCmd();

    // act
    final var currentConfig = ConfigMapper.mapFrom(List.of(config));

    // assert
    assertNotNull(currentConfig);
    assertTrue(currentConfig.isHelpCmd());
  }

}
