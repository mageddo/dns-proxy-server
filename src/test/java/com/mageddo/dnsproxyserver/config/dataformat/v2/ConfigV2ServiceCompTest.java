package com.mageddo.dnsproxyserver.config.dataformat.v2;

import javax.inject.Inject;

import com.mageddo.dnsproxyserver.config.configurer.di.Context;

import org.junit.jupiter.api.Test;

import dagger.sheath.junit.DaggerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(component = Context.class)
class ConfigV2ServiceCompTest {

  @Inject
  ConfigV2Service service;

  @Test
  void mustPutDaosInTheExpectedOrder() {
    // arrange

    // act
    final var names = this.service.findConfigNames();

    // assert
    assertEquals("[ConfigDAOLegacyEnv, ConfigDAOJson, ConfigDAOCmdArgs]", names.toString());
  }
}
