package com.mageddo.dnsproxyserver.config;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mageddo.dnsproxyserver.config.Config.Entry.Type;

public class ConfigEntryTypes {

  public static boolean isNot(Type current, Type... possible) {
    return !is(current, possible);
  }

  public static boolean is(Type current, Type... possible) {
    if (current == null) {
      return false;
    }
    return Stream
        .of(possible)
        .collect(Collectors.toSet())
        .contains(current);
  }

  public static boolean isNot(Integer code, Type... types) {
    return !is(Type.of(code), types);
  }
}
