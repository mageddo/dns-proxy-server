package com.mageddo.dnsproxyserver.utils;

public class Classes {
  public static String findSimpleName(Object o) {
    return o.getClass()
        .getSimpleName();
  }
}
