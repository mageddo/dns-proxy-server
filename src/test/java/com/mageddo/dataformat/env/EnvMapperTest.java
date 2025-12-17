package com.mageddo.dataformat.env;

import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3EnvMapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3EnvTemplates;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import com.mageddo.utils.TestUtils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvMapperTest {

  final EnvMapper mapper = new EnvMapper();

  @Test
  void mustConvertEnvVariablesToJsonStructure() {

    final var env = ConfigV3EnvTemplates.build();

    final var expected = TestUtils.sortJson(ConfigV3Templates.defaultJson_2025_12());

    final var json = this.mapper.toJson(env, ConfigV3EnvMapper.PREFIX);
    final var actual = TestUtils.sortJson(json);

    assertEquals(expected, actual);
  }
}
