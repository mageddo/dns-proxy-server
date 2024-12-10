package com.mageddo.dnsproxyserver.config;

import com.mageddo.net.IpAddr;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class SolverRemote {

  private Boolean active;

  private CircuitBreakerStrategyConfig circuitBreaker;

  @Builder.Default
  private List<IpAddr> dnsServers = new ArrayList<>();

}
