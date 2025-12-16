package com.mageddo.dnsproxyserver.config.dataformat.v3.file;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config;

import com.mageddo.dnsproxyserver.config.application.Configs;

import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.JsonConfigDAO;
import com.mageddo.dnsproxyserver.config.dataformat.v3.dataprovider.YamlConfigDAO;
import com.mageddo.utils.Files;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConfigFileReader {

  private final JsonConfigDAO jsonConfigDAO;
  private final YamlConfigDAO jsonConfigDAO;

  public Config read() {
    final var path = this.findConfigPath();
    final var extension = Files.findExtension(path);

  }


  public Path findConfigPath() {
    return Configs
        .getInstance()
        .getConfigPath();
  }
}
