package com.mageddo.dnsproxyserver.solver.remote;

import com.mageddo.dnsproxyserver.solver.Resolver;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResolverStats {

  private Resolver resolver;
  private CircuitStatus circuitStatus;

  public boolean isValidToUse() {
    return !CircuitStatus.OPEN.equals(this.circuitStatus);
  }
}
