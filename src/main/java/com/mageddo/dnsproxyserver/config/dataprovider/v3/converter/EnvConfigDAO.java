package com.mageddo.dnsproxyserver.config.dataprovider.v3.converter;

import com.mageddo.dataformat.env.EnvMapper;
import com.mageddo.dnsproxyserver.config.dataprovider.v3.ConfigV3;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EnvConfigDAO implements ConfigDAO {

  private static final String PREFIX = "DPS_";

  private final EnvMapper envMapper;
  private final JsonConfigDAO jsonConverter;

  @Override
  public ConfigV3 find() {
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
