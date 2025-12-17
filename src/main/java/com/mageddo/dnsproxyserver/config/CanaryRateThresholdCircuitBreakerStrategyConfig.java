package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CanaryRateThresholdCircuitBreakerStrategyConfig implements CircuitBreakerStrategyConfig {

  private float failureRateThreshold;
  private int minimumNumberOfCalls;
  private int permittedNumberOfCallsInHalfOpenState;

  @Override
  public Type getType() {
    return Type.CANARY_RATE_THRESHOLD;
  }
}
