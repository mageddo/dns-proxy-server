package com.mageddo.dnsproxyserver.dataprovider;

import ch.qos.logback.classic.Level;
import com.mageddo.commons.io.IoUtils;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataprovider.mapper.ConfigFieldsValuesMapper;
import com.mageddo.logback.LogbackUtils;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Slf4j
@Default
@Singleton
public class LogSettingsDAOSlf4j implements LogSettingsDAO {

  @Override
  public void setupLogFile(Config config) {
    final var logFile = ConfigFieldsValuesMapper.mapLogFileFrom(config.getLogFile());
    if (logFile == null) {
      disableLogging();
    } else if (isLogFileConfig(logFile)) {
      loadLoggingSettingsFromSpecifiedFile(logFile);
    }
  }

  @Override
  public void setupLogLevel(Config config) {
    if (isSpecificLogLevel(config)) {
      changeLogLevelToSpecifiedFromConfig(config);
    }
  }

  static void changeLogLevelToSpecifiedFromConfig(Config config) {
    LogbackUtils.changeLogLevel("com.mageddo", config.getLogLevel().toLogbackLevel());
  }

  static boolean isSpecificLogLevel(Config config) {
    return config.getLogLevel() != null;
  }

  private static boolean isLogFileConfig(String logFile) {
    return !equalsIgnoreCase(logFile, "console");
  }

  private static void loadLoggingSettingsFromSpecifiedFile(String logFile) {
    log.info("status=swapLogToFile, file={}", logFile);
    System.setProperty("dpsLogFile", logFile);
    LogbackUtils.replaceConfig(IoUtils.getResourceAsStream("/logback-file.xml"));
  }

  private static void disableLogging() {
    LogbackUtils.changeRootLogLevel(Level.OFF);
  }
}
