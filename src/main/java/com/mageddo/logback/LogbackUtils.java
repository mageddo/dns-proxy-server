package com.mageddo.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LogbackUtils {

  private LogbackUtils() {
  }

  public static void changeRootLogLevel(Level level) {
    final var root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    root.setLevel(level);
  }

  public static boolean changeLogLevel(String name, Level level) {
    final var logger = (Logger) LoggerFactory.getLogger(name);
    if (logger == null) {
      return false;
    }
    logger.setLevel(level);
    return true;
  }

}
