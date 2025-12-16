package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonYamlMapperTest {

  @Test
  void yamlAndJsonParsingMustGenerateSameVo() {

    final var json = ConfigV3Templates.buildJson();
    final var yaml = ConfigV3Templates.buildYaml();

    final var jsonParsed = ConfigV3JsonMapper.of(json);
    final var yamlParsed = ConfigV3YamlMapper.of(yaml);

    assertEquals(jsonParsed, yamlParsed);

  }

}
