package com.mageddo.dnsproxyserver.server.rest.reqres;

import com.mageddo.dnsproxyserver.solver.CacheEntry;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class CacheEntryResV1 {

  private String key;
  private String ttl;
  private String expiresAt;

  public static List<CacheEntryResV1> of(List<CacheEntry> entries) {
    return entries
      .stream()
      .map(CacheEntryResV1::of)
      .collect(Collectors.toList());
  }

  public static CacheEntryResV1 of(CacheEntry entry) {
    return new CacheEntryResV1()
      .setKey(entry.getKey())
      .setTtl(String.valueOf(entry.getTtl()))
      .setExpiresAt(String.valueOf(entry.getExpiresAt()))
      ;
  }

  public static Map<String, Map<String, CacheEntryResV1>> of(Map<String, Map<String, CacheEntry>> cache) {
    final var m = new HashMap<String, Map<String, CacheEntryResV1>>();
    cache
      .keySet()
      .forEach(k -> m.computeIfAbsent(k, (k_) -> {
        final var v = new HashMap<String, CacheEntryResV1>();
        cache
          .get(k)
          .forEach((k2, v2) -> {
            v.put(k2, of(v2));
          });
        return v;
      }));
    return m;
  }
}
