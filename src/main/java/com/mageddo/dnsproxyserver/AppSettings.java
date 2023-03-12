package com.mageddo.dnsproxyserver;

import ch.qos.logback.classic.Level;
import com.mageddo.commons.io.IoUtils;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.logback.LogbackUtils;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Slf4j
public class AppSettings {

  static void setup(Config config) {

    final var logFile = Configs.parseLogFile(config.getLogFile());
    if (logFile == null) {
      LogbackUtils.changeRootLogLevel(Level.OFF);
    } else if (!equalsIgnoreCase(logFile, "console")) {
      log.info("status=swapLogToFile, file={}", logFile);
      System.setProperty("dpsLogFile", logFile);
      LogbackUtils.replaceConfig(IoUtils.getResourceAsStream("/logback-file.xml"));
    }

    if (config.getLogLevel() != null) {
      LogbackUtils.changeLogLevel("com.mageddo", config.getLogLevel().toLogbackLevel());
    }

  }
}
