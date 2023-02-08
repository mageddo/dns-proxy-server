package com.mageddo.dnsproxyserver.quarkus;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.configuration.ConfigUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class Quarkus {
  public static boolean isTest() {
    return ConfigUtils
        .getProfiles()
        .contains("test")
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
