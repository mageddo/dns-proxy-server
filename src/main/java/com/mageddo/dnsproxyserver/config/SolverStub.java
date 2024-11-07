package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SolverStub {
  private String domainName;
}
