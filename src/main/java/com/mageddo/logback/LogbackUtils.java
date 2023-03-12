package com.mageddo.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class LogbackUtils {

  private LogbackUtils() {
  }

  public static void changeRootLogLevel(Level level) {
    final var root = getLoggerImpl(org.slf4j.Logger.ROOT_LOGGER_NAME);
    root.setLevel(level);
  }

  public static boolean changeLogLevel(String name, Level level) {
    final var logger = getLoggerImpl(name);
    if (logger == null) {
      return false;
    }
    logger.setLevel(level);
    return true;
  }

  public static Level getLogLevel(String name){
    return getLoggerImpl(name).getLevel();
  }

  public static void replaceConfig(InputStream configFileIn) {
    try {
      final var context = (LoggerContext) LoggerFactory.getILoggerFactory();
      final var configurator = new JoranConfigurator();
      configurator.setContext(context);
      context.reset();
      configurator.doConfigure(configFileIn);
    } catch (JoranException e) {
      throw new RuntimeException(e);
    }
  }

  private static Logger getLoggerImpl(String name) {
    return (Logger) LoggerFactory.getLogger(name);
  }

}
