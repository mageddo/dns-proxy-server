package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.JsonConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.YamlConfigDAO;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.templates.ConfigV3Templates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigDAOTest {

  private final JsonConfigDAO jsonParser = new JsonConfigDAO();
  private final YamlConfigDAO yamlParser = new YamlConfigDAO();

  @Test
  void yamlAndJsonParsingMustGenerateSameVo(){

    final var json = ConfigV3Templates.buildJson();
    final var yaml = ConfigV3Templates.buildYaml();

    final var jsonParsed = this.jsonParser.parse(json);
    final var yamlParsed = this.yamlParser.parse(yaml);

    assertEquals(jsonParsed, yamlParsed);

  }

}
