package com.mageddo.dnsproxyserver.utils;

import java.util.List;

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

  public static Integer firstPositive(List<Integer> list) {
    return firstPositive(list.toArray(Integer[]::new));
  }

  public static Integer firstPositive(Integer... arr) {
    for (final var v : arr) {
      if (v != null && v > 0) {
        return v;
      }
    }
    return null;
  }
}
