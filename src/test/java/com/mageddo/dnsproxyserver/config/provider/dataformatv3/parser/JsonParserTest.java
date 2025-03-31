package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonParserTest {

  JsonParser parser = new JsonParser();

  @Test
  void mustParseAndMarshalWithTheExactSameContent() {

    final var json = ConfigV3Templates.buildJson();

    final var parsed = parser.parse(json);
    final var marshalled = parser.marshal(parsed);
    final var marshalledParsed = parser.parse(json);

    assertEquals(json, marshalled);
    assertEquals(parsed, marshalledParsed);

  }
}
