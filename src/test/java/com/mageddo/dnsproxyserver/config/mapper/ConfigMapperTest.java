package com.mageddo.dnsproxyserver.config.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.ConfigTemplates;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


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

}
