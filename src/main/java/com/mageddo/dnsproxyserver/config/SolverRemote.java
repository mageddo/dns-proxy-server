package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SolverRemote {

  private Boolean active;

  private CircuitBreaker circuitBreaker;
}
