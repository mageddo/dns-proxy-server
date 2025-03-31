package com.mageddo.dnsproxyserver.config.provider.dataformatv3.parser;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;
import com.mageddo.json.JsonUtils;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class JsonParser implements Parser {

  @Override
  public ConfigV3 parse() {
    return parse("");
  }

  public static ConfigV3 parse(String json) {
    return JsonUtils.readValue(json, ConfigV3.class);
  }

  @Override
  public String marshal(ConfigV3 config) {
    return JsonUtils.prettyWriteValueAsString(config);
  }

  @Override
  public int priority() {
    return 1;
  }
}
