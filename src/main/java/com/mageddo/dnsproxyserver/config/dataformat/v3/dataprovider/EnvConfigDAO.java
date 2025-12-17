package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigMapper;
import com.mageddo.dnsproxyserver.config.dataformat.v3.mapper.ConfigV3EnvMapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EnvConfigDAO implements ConfigDAO {

  private final ConfigV3EnvMapper envMapper;

  @Override
  public Config find() {
    return ConfigMapper.of(this.envMapper.ofSystemEnv(), Config.Source.ENV);
  }

  @Override
  public int priority() {
    return 0;
  }
}
