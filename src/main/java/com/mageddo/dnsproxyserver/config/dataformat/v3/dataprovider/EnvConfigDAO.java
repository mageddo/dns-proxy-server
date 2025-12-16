package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3EnvMapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EnvConfigDAO implements ConfigDAO {

  private final ConfigV3EnvMapper envMapper;

  @Override
  public ConfigV3 find() {
    return this.envMapper.ofSystemEnv();
  }

  @Override
  public int priority() {
    return 0;
  }
}
