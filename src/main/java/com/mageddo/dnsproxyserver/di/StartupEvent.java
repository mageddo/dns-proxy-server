package com.mageddo.dnsproxyserver.di;

import java.util.Collection;
import java.util.Objects;

public interface StartupEvent {

  static boolean exists(Collection<StartupEvent> events, Class<?> clazz) {
    return lookup(events, clazz) != null;
  }

  static <T> T lookup(Collection<StartupEvent> events, Class<T> clazz) {
    return (T) events
      .stream()
      .filter(it -> Objects.equals(clazz, it.getClass()))
      .findFirst()
      .orElse(null)
      ;
  }

  void onStart();
}
