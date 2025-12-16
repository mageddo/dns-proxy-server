package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;

import com.mageddo.dnsproxyserver.config.dataformat.v2.cmdargs.ConfigDAOCmdArgs;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOCmdArgsDelegate implements ConfigDAO {

  private final ConfigDAOCmdArgs delegate;

  @Override
  public Config find() {
    return this.delegate.find();
  }

  @Override
  public int priority() {
    return 3;
  }
}
