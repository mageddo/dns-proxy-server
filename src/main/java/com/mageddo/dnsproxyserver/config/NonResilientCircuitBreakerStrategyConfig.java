package com.mageddo.dnsproxyserver.config;

public class NonResilientCircuitBreakerStrategyConfig implements CircuitBreakerStrategyConfig {
  @Override
  public Name name() {
    return Name.NON_RESILIENT;
  }
}
