package com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.dataprovider.predicate;

import java.util.Objects;
import java.util.function.Predicate;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.dataformat.v2.jsonv1v2.vo.ConfigJsonV2;

public class EntryPredicate {

  public static Predicate<Config.Entry> nameMatches(String hostname) {
    return it -> it.getHostname()
        .matches(String.format(".*%s.*", hostname));
  }

  public static Predicate<ConfigJsonV2.Entry> exactName(String hostname) {
    return entry -> Objects.equals(entry.getHostname(), hostname);
  }

  public static Predicate<ConfigJsonV2.Entry> byId(Long id) {
    return entry -> Objects.equals(entry.getId(), id);
  }
}
