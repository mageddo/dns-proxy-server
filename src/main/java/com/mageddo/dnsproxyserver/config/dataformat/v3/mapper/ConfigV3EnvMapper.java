package com.mageddo.dnsproxyserver.config.dataformat.v3.mapper;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dataformat.env.EnvMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigV3EnvMapper {

  public static final String PREFIX = "DPS_";

  private final EnvMapper envMapper;

  public ConfigV3 ofSystemEnv() {
    return this.of(System.getenv());
  }

  public ConfigV3 of(Map<String, String> source) {
    final var copy = new HashMap<>(source);
    copy.put("DPS_VERSION", "3");
    final var json = this.envMapper.toJson(copy, PREFIX);
    return ConfigV3JsonMapper.of(json);
  }

}
