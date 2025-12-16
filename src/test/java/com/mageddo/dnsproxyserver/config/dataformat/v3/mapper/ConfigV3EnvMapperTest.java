package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dataformat.env.EnvMapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3EnvTemplates;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigV3EnvMapperTest {

  final ConfigV3EnvMapper mapper = new ConfigV3EnvMapper(new EnvMapper());

  @Test
  void mustFindEnvironmentIntoConfig() {
    // Arrange
    final var expected = ConfigV3Templates.build();
    final var env = ConfigV3EnvTemplates.build();

    // Act
    final var actual = this.mapper.of(env);

    // Assert
    assertEquals(expected, actual);
  }
}
