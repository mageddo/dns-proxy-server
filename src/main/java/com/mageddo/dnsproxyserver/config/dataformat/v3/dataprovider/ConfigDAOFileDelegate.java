package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.file.ConfigFileDAO;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOFileDelegate implements ConfigDAO {

  private final ConfigFileDAO configFileDAO;

  @Override
  public Config find() {
    return this.configFileDAO.find();
  }

  @Override
  public int priority() {
    return 1;
  }
}
