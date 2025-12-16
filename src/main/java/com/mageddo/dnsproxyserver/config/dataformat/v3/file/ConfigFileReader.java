package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.utils.Files;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigFileReader {

  private final ConfigDAOFactory configDAOFactory;
  private final ConfigFilePathDAO configFilePathDAO;

  public Config read() {
    final var path = this.findConfigPath();
    final var extension = Files.findExtension(path);
    return this.configDAOFactory.findByExtension(extension)
        .find();
  }


  public Path findConfigPath() {
    return Configs
        .getInstance()
        .getConfigPath();
  }
}
