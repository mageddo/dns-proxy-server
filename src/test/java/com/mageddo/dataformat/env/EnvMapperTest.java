package com.mageddo.dataformat.env;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3EnvTemplates;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import com.mageddo.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvMapperTest {

  private final EnvMapper mapper = new EnvMapper();

  @Test
  void mustConvertEnvVariablesToJsonStructure() {
    // Arrange
    final var env = ConfigV3EnvTemplates.build();
    final var expected = JsonUtils.readTree(ConfigV3Templates.buildJson());

    // Act
    final var json = this.mapper.toJson(env, "DPS_");
    final var actual = JsonUtils.readTree(json);

    // Assert
    assertEquals(expected, actual);
  }
}
