package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;
import com.mageddo.json.JsonUtils;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class JsonConverter implements Converter {

  @Override
  public ConfigV3 parse() {
    return parse("");
  }

  public ConfigV3 parse(String json) {
    return JsonUtils.readValue(json, ConfigV3.class);
  }

  @Override
  public String serialize(ConfigV3 config) {
    return JsonUtils.prettyWriteValueAsString(config);
  }

  @Override
  public int priority() {
    return 1;
  }
}
