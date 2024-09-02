package com.mageddo.dnsproxyserver.config;

public interface CircuitBreakerStrategy {

  Name name();

  enum Name {
    STATIC_THRESHOLD,
    CANARY_RATE_THRESHOLD,
  }
}
