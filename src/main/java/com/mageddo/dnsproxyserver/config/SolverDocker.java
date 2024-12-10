package com.mageddo.dnsproxyserver.config;


import lombok.Builder;
import lombok.Value;

import java.net.URI;

@Value
@Builder
public class SolverDocker {

  private URI dockerDaemonUri;
  private Boolean registerContainerNames;
  private String domain;
  private DpsNetwork dpsNetwork;

  @Value
  @Builder
  public static class DpsNetwork {
    private Boolean autoCreate;
    private Boolean autoConnect;
  }
}
