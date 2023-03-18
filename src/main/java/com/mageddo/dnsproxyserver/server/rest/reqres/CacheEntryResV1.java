package com.mageddo.dnsproxyserver.server.rest.reqres;

import com.mageddo.dnsproxyserver.server.dns.solver.CacheEntry;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class CacheEntryResV1 {

  private String key;
  private String ttl;
  private String createdAt;

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
      .setCreatedAt(String.valueOf(entry.getCreatedAt()))
      ;
  }

}
