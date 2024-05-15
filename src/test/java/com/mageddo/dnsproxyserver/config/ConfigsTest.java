package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.dataprovider.mapper.DataproviderVoToConfigDomainMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testing.templates.ConfigFlagTemplates;
import testing.templates.EnvTemplates;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
