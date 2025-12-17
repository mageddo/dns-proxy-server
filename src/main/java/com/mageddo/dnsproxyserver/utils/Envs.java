package com.mageddo.dnsproxyserver.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class Envs {

  public static Path getPathOrDefault(String env, Path def) {
    return Optional
        .ofNullable(getPathOrNull(env))
        .orElse(def);
  }

  public static Path getPathOrNull(String env) {
    final var v = System.getenv(env);
    if (StringUtils.isBlank(v)) {
      return null;
    }
    return Paths.get(v);
  }

  public static String getStringOrNull(String env) {
    final var v = System.getenv(env);
    if (StringUtils.isBlank(v)) {
      return null;
    }
    return v;
  }

  public static Boolean getBooleanOrNull(String env) {
    final var v = StringUtils.trimToEmpty(System.getenv(env));
    return parseBoolean(v);
  }

  static Boolean parseBoolean(String v) {
    if (StringUtils.isBlank(v)) {
      return null;
    }
    return Objects.equals(v, "1") || StringUtils.equalsIgnoreCase(v, "true");
  }

  public static String getStringOrDefault(String env, String def) {
    return StringUtils.defaultIfBlank(System.getenv(env), def);
  }

  public static Integer getIntegerOrNull(String env) {
    return getIntegerOrDefault(env, null);
  }

  public static Integer getIntegerOrDefault(String env, Integer def) {
    if (StringUtils.isBlank(System.getenv(env))) {
      return def;
    }
    return Integer.parseInt(System.getenv(env));
  }
}
