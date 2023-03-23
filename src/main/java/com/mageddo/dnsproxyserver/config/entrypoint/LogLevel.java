package com.mageddo.dnsproxyserver.config.entrypoint;

import ch.qos.logback.classic.Level;
import org.apache.commons.lang3.StringUtils;

public enum LogLevel {

  ERROR,
  WARNING("WARN"),
  INFO,
  DEBUG,
  TRACE,
  ;

  private final String slf4jName;

  LogLevel() {
    this.slf4jName = null;
  }

  LogLevel(String slf4jName) {
    this.slf4jName = slf4jName;
  }

  public String getSlf4jName() {
    return StringUtils.firstNonBlank(this.slf4jName, this.name());
  }

  @Override
  public String toString() {
    return this.name();
  }

  public Level toLogbackLevel() {
    return Level.convertAnSLF4JLevel(org.slf4j.event.Level.valueOf(this.getSlf4jName()));
  }
}
