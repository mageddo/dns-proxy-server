package com.mageddo.dnsproxyserver.server.dns.solver;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CacheEntry {
  private String key;
  private Duration ttl;
  private LocalDateTime createdAt;
}
