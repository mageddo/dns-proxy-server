package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YamlParserTest {

  YamlParser parser = new YamlParser();

  @Test
  void mustParseAndMarshalWithTheExactSameContent() {

    final var yaml = ConfigV3Templates.buildYaml();

    final var parsed = parser.parse(yaml);
    final var marshalled = parser.marshal(parsed);
    final var marshalledParsed = parser.parse(yaml);

    assertEquals(yaml, marshalled);
    assertEquals(parsed, marshalledParsed);

  }
}
