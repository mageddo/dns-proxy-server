package com.mageddo.dnsproxyserver.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Splits {
  public static final String COMMA_SEPARATED = "\s?,\s?";

  public static List<Path> splitToPaths(String v) {
    return splitToList(v)
      .stream()
      .map(Paths::get)
      .toList()
      ;
  }

  public static List<String> splitToList(String v) {
    if (v == null) {
      return null;
    }
    return Arrays
      .stream(v.split(COMMA_SEPARATED))
      .toList();
  }
}
