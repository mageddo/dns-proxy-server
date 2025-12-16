package com.mageddo.dnsproxyserver.config.predicate;

import java.util.Objects;
import java.util.function.Predicate;

import com.mageddo.dnsproxyserver.config.Config;

public class EntryPredicate {

  public static Predicate<Config.Entry> nameMatches(String hostname) {
    return it -> it.getHostname().matches(String.format(".*%s.*", hostname));
  }

  public static Predicate<Config.Entry> exactName(String hostname) {
    return entry -> Objects.equals(entry.getHostname(), hostname);
  }

  public static Predicate<Config.Entry> byId(Long id) {
    return entry -> Objects.equals(entry.getId(), id);
  }
}
