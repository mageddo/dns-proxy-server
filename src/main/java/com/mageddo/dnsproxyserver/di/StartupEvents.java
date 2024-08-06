package com.mageddo.dnsproxyserver.di;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Objects;

@Slf4j
public class StartupEvents {
  public static boolean exists(Collection<StartupEvent> events, Class<?> classToFind) {
    return lookup(events, classToFind) != null;
  }

  public static <T> T lookup(Collection<StartupEvent> events, Class<T> classToFind) {
    final var found = events
      .stream()
      .filter(it -> Objects.equals(classToFind, it.getClass()))
      .findFirst()
      .orElse(null);
    log.trace("found={}, classToFind={}", found, classToFind);
    return (T) found;
  }
}
