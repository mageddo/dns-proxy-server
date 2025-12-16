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

  private final ConfigFilePathDAO configFilePathDAO;

  public Config read() {

  }


  public Path findConfigPath() {
    return Configs
        .getInstance()
        .getConfigPath();
  }
}
