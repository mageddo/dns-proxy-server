package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.utils.Files;
import org.junit.jupiter.api.Test;
import testing.templates.EnvTemplates;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigsTest {

  @Test
  void mustGenerateEnvHostnameIdWhenIsNull() {
    // arrange

    // act
    final var env = EnvTemplates.buildWithoutId();
    final var firstEntry = env
      .getEntries()
      .stream()
      .findFirst()
      .get();

    // assert
    assertNotNull(firstEntry.getId());
    final var currentNanoTime = System.nanoTime();
    assertTrue(
      firstEntry.getId() < currentNanoTime,
      String.format("id=%s, currentTimeInMillis=%s", firstEntry.getId(), currentNanoTime)
    );
  }


  @Test
  void mustCreateDefaultConfigFileOnRandomPathWhenTesting(){

    final var config = Configs.getInstance();

    final var configPath = config.getConfigPath();

    assertTrue(Files.exists(configPath));
    assertThat(configPath.toString(), containsString("-junit"));
  }
}
