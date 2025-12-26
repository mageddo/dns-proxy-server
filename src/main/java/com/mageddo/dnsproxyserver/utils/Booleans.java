package com.mageddo.dnsproxyserver.utils;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

public class Booleans {

  public static boolean getOrDefault(Boolean value, boolean def) {
    if (value == null) {
      return def;
    }
    return value;
  }

  public static Boolean reverseWhenNotNull(Boolean value) {
    if (value == null) {
      return null;
    }
    return !value;
  }

    public static Boolean parse(String v) {
      if (StringUtils.isBlank(v)) {
        return null;
      }
      return Objects.equals(v, "1") || Strings.CI.equals(v, "true");
    }
}
