package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YamlConverterTest {

  YamlConverter parser = new YamlConverter();

  @Test
  void mustParseAndSerializeWithTheExactSameContent() {

    final var yaml = ConfigV3Templates.buildYaml();

    final var parsed = parser.parse(yaml);
    final var marshalled = parser.serialize(parsed);
    final var marshalledParsed = parser.parse(yaml);

    assertEquals(yaml, marshalled);
    assertEquals(parsed, marshalledParsed);

  }
}
