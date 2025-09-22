package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dataformat.env.EnvMapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3EnvTemplates;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvConverterTest {

  private final EnvConverter converter = new EnvConverter(new EnvMapper(), new JsonConverter());

  @Test
  void mustParseEnvironmentIntoConfig() {
    // Arrange
    final var env = ConfigV3EnvTemplates.build();
    final var expected = new ConfigV3Templates().build();

    // Act
    final var actual = this.converter.parse(env);

    // Assert
    assertEquals(expected, actual);
  }
}
