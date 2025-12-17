package com.mageddo.dnsproxyserver.config.mapper;

import com.mageddo.dnsproxyserver.config.Config;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogLevelMapper {
  public static Config.Log.Level mapLogLevelFrom(String logLevelName) {
    final var level = EnumUtils.getEnumIgnoreCase(Config.Log.Level.class, logLevelName);
    if (StringUtils.isNotBlank(logLevelName) && level == null) {
      log.warn("status=couldntParseLogLevel, action=changesWillTakeNoEffect, proposedValue={}",
          logLevelName
      );
    }
    return level;
  }

  public static String mapLogFileFrom(String v) {
    if (StringUtils.isBlank(v)) {
      return null;
    }
    return switch (StringUtils.lowerCase(v)) {
      case "true" -> "/var/log/dns-proxy-server.log";
      case "false" -> null;
      default -> v;
    };
  }
}
