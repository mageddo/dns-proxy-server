package com.mageddo.dnsproxyserver.solver;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CacheEntry {
  private String key;
  private Duration ttl;
  private LocalDateTime expiresAt;
}
