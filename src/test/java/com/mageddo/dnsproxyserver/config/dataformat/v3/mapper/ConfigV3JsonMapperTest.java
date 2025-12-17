package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigV3JsonMapperTest {

  @Test
  void mustFindAndSerializeWithTheExactSameContent() {

    final var json = ConfigV3Templates.buildJson();

    final var parsed = ConfigV3JsonMapper.of(json);
    final var marshalledParsed = ConfigV3JsonMapper.of(ConfigV3JsonMapper.toJson(parsed));

    assertThat(parsed)
        .usingRecursiveComparison()
        .isEqualTo(marshalledParsed);

  }
}
