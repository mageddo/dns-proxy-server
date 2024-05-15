package com.mageddo.dnsproxyserver.utils;

public class Numbers {
  public static Integer positiveOrDefault(Integer v, Integer def) {
    if (v == null || v <= 0) {
      return def;
    }
    return v;
  }
  public static Integer positiveOrNull(Integer v) {
    if (v == null || v <= 0) {
      return null;
    }
    return v;
  }
}
