package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import com.mageddo.dnsproxyserver.config.dataprovider.v3.converter.YamlConfigDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YamlConfigDAOTest {

  YamlConfigDAO parser = new YamlConfigDAO();

  @Test
  void mustFindAndSerializeWithTheExactSameContent() {

    final var yaml = ConfigV3Templates.buildYaml();

    final var parsed = parser.parse(yaml);
    final var marshalled = parser.serialize(parsed);
    final var marshalledParsed = parser.parse(yaml);

    assertEquals(yaml, marshalled);
    assertEquals(parsed, marshalledParsed);

  }
}
