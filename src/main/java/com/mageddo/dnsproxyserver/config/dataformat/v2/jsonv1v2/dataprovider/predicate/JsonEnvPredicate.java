package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.predicate;

import java.util.function.Predicate;

import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJsonV2;

import org.apache.commons.lang3.StringUtils;

public class JsonEnvPredicate {
  public static Predicate<ConfigJsonV2.Env> byName(String name) {
    return (it) -> StringUtils.equalsIgnoreCase(it.getName(), name);
  }

  public static Predicate<ConfigJsonV2.Env> nameIsNot(String name) {
    return byName(name).negate();
  }
}
