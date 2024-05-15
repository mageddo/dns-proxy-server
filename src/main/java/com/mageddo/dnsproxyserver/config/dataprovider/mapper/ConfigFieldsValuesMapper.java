package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ConfigFieldsValuesMapper {
  public static LogLevel mapLogLevelFrom(String logLevelName) {
    final var level = EnumUtils.getEnumIgnoreCase(LogLevel.class, logLevelName);
    if (StringUtils.isNotBlank(logLevelName) && level == null) {
      log.warn("status=couldntParseLogLevel, action=changesWillTakeNoEffect, proposedValue={}", logLevelName);
    }
    return level;
  }

  public static String mapLogFileFrom(String v) {
    return switch (StringUtils.lowerCase(v)) {
      case "true" -> "/var/log/dns-proxy-server.log";
      case "false" -> null;
      default -> v;
    };
  }
}
