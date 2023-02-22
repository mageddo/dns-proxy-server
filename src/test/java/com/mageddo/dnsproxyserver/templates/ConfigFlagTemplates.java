package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.entrypoint.ConfigFlag;

import java.nio.file.Path;

public class ConfigFlagTemplates {
  public static ConfigFlag build() {
    return ConfigFlag.parse(new String[]{});
  }

  public static ConfigFlag defaultWithConfigPath(Path path) {
    return ConfigFlag.parse(new String[]{"--conf-path", path.toString()});
  }
}
