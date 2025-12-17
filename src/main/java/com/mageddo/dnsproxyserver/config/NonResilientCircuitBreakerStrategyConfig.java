package com.mageddo.dnsproxyserver.config;

public class NonResilientCircuitBreakerStrategyConfig implements CircuitBreakerStrategyConfig {
  @Override
  public Type getType() {
    return Type.NON_RESILIENT;
  }
}
