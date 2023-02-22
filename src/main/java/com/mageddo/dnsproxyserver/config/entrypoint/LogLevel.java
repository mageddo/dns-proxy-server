package com.mageddo.dnsproxyserver.config.entrypoint;

import org.apache.commons.lang3.StringUtils;

public enum LogLevel {
  ERROR,
  WARNING("WARN"),
  INFO,
  DEBUG,
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
}
