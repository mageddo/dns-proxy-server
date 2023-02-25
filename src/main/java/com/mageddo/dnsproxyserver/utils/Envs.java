package com.mageddo.dnsproxyserver.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

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
    if(StringUtils.isBlank(v)){
      return null;
    }
    return Objects.equals(v, "1");
  }

  public static String getStringOrDefault(String env, String def) {
    return StringUtils.defaultIfBlank(System.getenv(env), def);
  }
}
