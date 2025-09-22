package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonConverterTest {

  JsonConverter parser = new JsonConverter();

  @Test
  void mustParseAndSerializeWithTheExactSameContent() {

    final var json = ConfigV3Templates.buildJson();

    final var parsed = parser.parse(json);
    final var marshalled = parser.serialize(parsed);
    final var marshalledParsed = parser.parse(json);

    assertEquals(json, marshalled);
    assertEquals(parsed, marshalledParsed);

  }
}
