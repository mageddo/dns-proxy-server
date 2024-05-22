package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SolverRemote {
  @NonNull
  private CircuitBreaker circuitBreaker;
}
