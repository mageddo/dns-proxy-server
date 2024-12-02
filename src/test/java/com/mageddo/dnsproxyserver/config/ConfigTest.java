package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.utils.Files;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

  @Test
  void mustDeleteConfigFile(){

    final var config = Configs.getInstance();

    final var configPath = config.getConfigPath();
    assertTrue(Files.exists(configPath));

    config.resetConfigFile();
    assertFalse(Files.exists(configPath));

  }
}
