package com.mageddo.dnsproxyserver.solver.cache;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CacheEntry {
  private String key;
  private Duration ttl;
  private LocalDateTime expiresAt;
}
