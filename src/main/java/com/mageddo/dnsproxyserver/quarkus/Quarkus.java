package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.utils.Tests;
import io.quarkus.arc.Arc;
import io.quarkus.runtime.configuration.ConfigUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class Quarkus {
  public static boolean isTest() {
    return ConfigUtils
      .getProfiles()
      .contains("test")
      || Tests.inTest()
      ;
  }

  public static <T> Set<T> beansOf(Class<T> clazz) {
    return Arc.container()
      .select(clazz)
      .stream()
      .collect(Collectors.toSet())
      ;
  }
}
