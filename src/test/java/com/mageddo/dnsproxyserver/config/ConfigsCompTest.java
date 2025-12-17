package com.mageddo.dnsproxyserver.config;

import java.nio.file.Files;

import javax.inject.Inject;

import com.mageddo.dnsproxyserver.config.configurer.di.Context;
import com.mageddo.dnsproxyserver.config.dataformat.v3.file.ConfigFilePathDAO;

import org.junit.jupiter.api.Test;

import dagger.sheath.junit.DaggerTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DaggerTest(component = Context.class)
class ConfigsCompTest {

  @Inject
  ConfigFilePathDAO dao;

  @Test
  void mustCreateDefaultConfigFileOnRandomPathWhenTesting() {

    final var path = this.dao.find();
    assertTrue(Files.exists(path));
    assertThat(path.toString(), containsString("-junit"));

  }
}
