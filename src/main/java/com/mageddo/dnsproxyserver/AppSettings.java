package com.mageddo.dnsproxyserver;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.logback.LogbackUtils;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class AppSettings {

  static void setup(Config config) {

    if (config.getLogLevel() != null) {
      LogbackUtils.changeLogLevel("com.mageddo", config.getLogLevel().toLogbackLevel());
    }

    final var logFile = Configs.parseLogFile(config.getLogFile());
    if (logFile == null) {
      System.setProperty("quarkus.log.console.enable", "false");
      System.setProperty("quarkus.log.file.enable", "false");
    } else if (!equalsIgnoreCase(logFile, "console")) {
      System.setProperty("quarkus.log.console.enable", "false");
      System.setProperty("quarkus.log.file.enable", "true");
      System.setProperty("quarkus.log.file.path", config.getLogFile());
    }
  }
}
