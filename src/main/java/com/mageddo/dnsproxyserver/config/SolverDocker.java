package com.mageddo.dnsproxyserver.config;


import lombok.Builder;
import lombok.Value;

import java.net.URI;

@Value
@Builder(toBuilder = true)
public class SolverDocker {

  private URI dockerDaemonUri;
  private Boolean registerContainerNames;
  private String domain;
  private DpsNetwork dpsNetwork;

  public boolean shouldAutoCreateDpsNetwork() {
    if (this.dpsNetwork == null) {
      return false;
    }
    return this.dpsNetwork.autoCreate;
  }

  public boolean shouldAutoConnect() {
    if (this.dpsNetwork == null) {
      return false;
    }
    return this.dpsNetwork.autoConnect;
  }

  @Value
  @Builder
  public static class DpsNetwork {
    private Boolean autoCreate;
    private Boolean autoConnect;
  }
}
