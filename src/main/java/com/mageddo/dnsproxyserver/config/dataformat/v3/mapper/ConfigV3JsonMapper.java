package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.JsonConfigs;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJson;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.json.JsonUtils;

public class ConfigV3JsonMapper {

  public static ConfigV3 of(String json) {
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
    return null;
  }

  public static String toJson(ConfigV3 config) {
    return JsonUtils.prettyWriteValueAsString(config);
  }
}
