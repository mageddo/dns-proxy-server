package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.json.JsonUtils;

public class ConfigV3JsonMapper {

  public static ConfigV3 of(String json) {
    final var tree = JsonUtils.readTree(json);
    final var version = tree.at("/version")
        .asInt(0);
    if (version != 3) {
      throw new IllegalArgumentException(String.format(
          "invalid version: %d, it must be 3", version
      ));
    }
    return JsonUtils.readValue(json, ConfigV3.class);
  }

  public static String toJson(ConfigV3 config) {
    return JsonUtils.prettyWriteValueAsString(config);
  }
}
