package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import java.nio.file.Path;

import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.mapper.ConfigJsonV2Mapper;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJson;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.json.JsonUtils;

import org.apache.commons.lang3.StringUtils;

public class ConfigV3JsonMapper {

  public static ConfigV3 of(String json) {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    final var tree = JsonUtils.readTree(json);
    final var version = tree.at("/version")
        .asInt(0);
    if (version == 1 || version == 2) {
      return of(JsonConfigs.loadConfig(json));
    } else if (version == 3) {
      return JsonUtils.readValue(json, ConfigV3.class);
    }
    throw new IllegalArgumentException(String.format(
        "invalid version: %d, it must be 1, 2 or 3", version
    ));
  }

  private static ConfigV3 of(ConfigJson config) {
    return ConfigMapper.toV3(ConfigJsonV2Mapper.toConfig(config, Path.of("/tmp/stub")));
  }

  public static String toJson(ConfigV3 config) {
    return JsonUtils.prettyWriteValueAsString(config);
  }
}
