package com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v3.file.ConfigFilePathDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.file.FileMapperFactory;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigDAOFileDelegate implements ConfigDAO {

  private final FileMapperFactory fileMapperFactory;
  private final ConfigFilePathDAO configFilePathDAO;

  @Override
  public Config find() {
    final var path = this.configFilePathDAO.find();
    return this.fileMapperFactory.of(path);
  }

  @Override
  public int priority() {
    return 1;
  }
}
