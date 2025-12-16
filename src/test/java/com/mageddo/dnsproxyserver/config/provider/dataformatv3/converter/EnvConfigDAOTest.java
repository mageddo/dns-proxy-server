package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dataformat.env.EnvMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.EnvConfigDAO;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3EnvTemplates;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.JsonConfigDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvConfigDAOTest {

  private final EnvConfigDAO converter = new EnvConfigDAO(new EnvMapper(), new JsonConfigDAO());

  @Test
  void mustFindEnvironmentIntoConfig() {
    // Arrange
    final var expected = ConfigV3Templates.build();
    final var env = ConfigV3EnvTemplates.build();

    // Act
    final var actual = this.converter.parse(env);

    // Assert
    assertEquals(expected, actual);
  }
}
