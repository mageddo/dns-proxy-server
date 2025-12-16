package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.JsonConfigDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonConfigDAOTest {

  JsonConfigDAO parser = new JsonConfigDAO();

  @Test
  void mustFindAndSerializeWithTheExactSameContent() {

    final var json = ConfigV3Templates.buildJson();

    final var parsed = parser.parse(json);
    final var marshalled = parser.serialize(parsed);
    final var marshalledParsed = parser.parse(json);

    assertEquals(json, marshalled);
    assertEquals(parsed, marshalledParsed);

  }
}
