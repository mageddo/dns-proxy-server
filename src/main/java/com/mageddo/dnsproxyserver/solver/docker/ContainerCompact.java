package com.mageddo.dnsproxyserver.solver.docker;

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
  String id;

  @NonNull
  String name;

  @NonNull
  Boolean dpsContainer;

  public boolean isNotDpsContainer() {
    return !this.dpsContainer;
  }
}
