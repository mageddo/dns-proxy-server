package com.mageddo.dnsproxyserver.utils;

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
}
