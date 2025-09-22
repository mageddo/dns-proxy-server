package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterTest {

  private final JsonConverter jsonParser = new JsonConverter();
  private final YamlConverter yamlParser = new YamlConverter();

  @Test
  void yamlAndJsonParsingMustGenerateSameVo(){

    final var json = ConfigV3Templates.buildJson();
    final var yaml = ConfigV3Templates.buildYaml();

    final var jsonParsed = this.jsonParser.parse(json);
    final var yamlParsed = this.yamlParser.parse(yaml);

    assertEquals(jsonParsed, yamlParsed);

  }

}
