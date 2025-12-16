package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.mapper.ConfigJsonV2Mapper;
import com.mageddo.utils.Files;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

  @Test
  void mustDeleteConfigFile(){

    final var config = Configs.getContext()
      .configService()
      .findCurrentConfig();

    final var configPath = config.getConfigPath();
    assertTrue(Files.exists(configPath));

    config.resetConfigFile();
    assertFalse(Files.exists(configPath));

  }


  @Test
  void thrownErrorWhenThereIsNoConfigFilePath(){

    final var config = ConfigJsonV2Mapper.toConfig(JsonConfigs.buildDefaultJsonConfig(), null);

    assertNull(config.getConfigPath());
    assertThrows(IllegalStateException.class, config::resetConfigFile);

  }
}
