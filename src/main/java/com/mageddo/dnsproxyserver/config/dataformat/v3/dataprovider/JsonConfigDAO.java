package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;

import lombok.NoArgsConstructor;

@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class JsonConfigDAO implements ConfigDAO {

  @Override
  public Config find() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int priority() {
    return 1;
  }
}
