package com.mageddo.dnsproxyserver.sandbox;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.exec.DaemonExecutor;

@Value
@Builder
public class Instance {

  @NonNull
  DaemonExecutor executor;

  public static Instance of(DaemonExecutor executor) {
    return Instance.builder()
      .executor(executor)
      .build()
      ;
  }
}
