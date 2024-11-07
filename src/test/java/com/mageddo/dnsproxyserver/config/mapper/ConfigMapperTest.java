package com.mageddo.dnsproxyserver.config.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.ConfigTemplates;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @Test
  void mustMapSolverStub() {
    // arrange
    final var theDefault = ConfigTemplates.defaultWithoutId();
    final var another = ConfigTemplates.acmeSolverStub();

    // act
    final var currentConfig = ConfigMapper.mapFrom(List.of(theDefault, another));

    // assert
    assertNotNull(currentConfig);

    final var solverStub = currentConfig.getSolverStub();
    assertNotNull(solverStub);
    assertEquals("acme", solverStub.getDomainName());
  }

}
