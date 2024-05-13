package com.mageddo.dnsproxyserver.server.dns.solver.docker;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Container with most basic props.
 */
@Value
@Builder
public class ContainerCompact {

  @NonNull
  private String id;

  @NonNull
  private String name;

  @NonNull
  private Boolean dpsContainer;

  public boolean isNotDpsContainer() {
    return !this.dpsContainer;
  }
}
