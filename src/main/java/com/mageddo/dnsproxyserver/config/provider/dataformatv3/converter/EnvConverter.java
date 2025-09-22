package com.mageddo.dnsproxyserver.config.provider.dataformatv3.converter;

import com.mageddo.dataformat.env.EnvMapper;
import com.mageddo.dnsproxyserver.config.provider.dataformatv3.ConfigV3;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EnvConverter implements Converter {

  private static final String PREFIX = "DPS_";

  private final EnvMapper envMapper;
  private final JsonConverter jsonConverter;

  @Override
  public ConfigV3 parse() {
    return this.parse(System.getenv());
  }

  ConfigV3 parse(Map<String, String> env) {
    final var json = this.envMapper.toJson(env, PREFIX);
    return this.jsonConverter.parse(json);
  }

  @Override
  public String serialize(ConfigV3 config) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int priority() {
    return 0;
  }
}
