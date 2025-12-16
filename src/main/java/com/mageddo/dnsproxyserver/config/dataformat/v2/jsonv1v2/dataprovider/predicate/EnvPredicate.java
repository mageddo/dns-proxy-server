package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.predicate;

import com.mageddo.dnsproxyserver.config.Config;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class EnvPredicate {
  public static Predicate<Config.Env> byName(String name) {
    return it -> StringUtils.equalsIgnoreCase(it.getName(), name);
  }
}
