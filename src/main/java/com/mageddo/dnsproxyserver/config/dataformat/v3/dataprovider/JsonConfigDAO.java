package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.dataformat.v3.ConfigV3;

import lombok.NoArgsConstructor;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class JsonConfigDAO implements ConfigDAO {

  @Override
  public ConfigV3 find() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int priority() {
    return 1;
  }
}
