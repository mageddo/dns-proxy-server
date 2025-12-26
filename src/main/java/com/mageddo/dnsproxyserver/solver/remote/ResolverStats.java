package com.mageddo.dnsproxyserver.solver.remote;

import com.mageddo.dnsproxyserver.solver.Resolver;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ResolverStats {

  @NonNull
  Resolver resolver;

  CircuitStatus circuitStatus;

  public boolean isValidToUse() {
    return !CircuitStatus.OPEN.equals(this.circuitStatus);
  }
}
