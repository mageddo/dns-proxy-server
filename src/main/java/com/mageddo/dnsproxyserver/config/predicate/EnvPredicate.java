package com.mageddo.dnsproxyserver.config.predicate;

import java.util.function.Predicate;

import com.mageddo.dnsproxyserver.config.Config;

import org.apache.commons.lang3.StringUtils;

public class EnvPredicate {
  public static Predicate<Config.Env> byName(String name) {
    return it -> StringUtils.equalsIgnoreCase(it.getName(), name);
  }

  public static Predicate<Config.Env> nameIsNot(String name) {
    return byName(name).negate();
  }
}
