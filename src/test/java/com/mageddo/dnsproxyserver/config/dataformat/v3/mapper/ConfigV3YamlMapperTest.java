package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigV3YamlMapperTest {

  @Test
  void mustFindAndSerializeWithTheExactSameContent() {

    final var yaml = ConfigV3Templates.buildYaml();

    final var parsed = ConfigV3YamlMapper.of(yaml);
    final var marshalledParsed = ConfigV3YamlMapper.of(ConfigV3YamlMapper.toYaml(parsed));

    assertThat(parsed)
        .usingRecursiveComparison()
        .isEqualTo(marshalledParsed);

  }
}
