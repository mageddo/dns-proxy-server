package com.mageddo.dnsproxyserver.config;

public interface CircuitBreakerStrategy {

  Name type();

  enum Name {
    STATIC_THRESHOLD,
    CANARY_RATE_THRESHOLD,
  }
}
