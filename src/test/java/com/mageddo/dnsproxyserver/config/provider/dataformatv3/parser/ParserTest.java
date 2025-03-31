package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

  private final JsonParser jsonParser = new JsonParser();
  private final YamlParser yamlParser = new YamlParser();

  @Test
  void yamlAndJsonParsingMustGenerateSameVo(){

    final var json = ConfigV3Templates.buildJson();
    final var yaml = ConfigV3Templates.buildYaml();

    final var jsonParsed = this.jsonParser.parse(json);
    final var yamlParsed = this.yamlParser.parse(yaml);

    assertEquals(jsonParsed, yamlParsed);

  }

}
