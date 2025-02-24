package com.mageddo.dnsproxyserver.config;


import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.BooleanUtils;

import java.net.URI;

@Value
@Builder(toBuilder = true)
public class SolverDocker {

  private URI dockerDaemonUri;
  private Boolean registerContainerNames;
  private String domain;
  private DpsNetwork dpsNetwork;
  private Boolean hostMachineFallback;

  public boolean shouldUseHostMachineFallback() {
    return BooleanUtils.toBoolean(hostMachineFallback);
  }

  public boolean shouldAutoCreateDpsNetwork() {
    if (this.dpsNetwork == null) {
      return false;
    }
    return this.dpsNetwork.shouldAutoCreate();
  }

  public boolean shouldAutoConnect() {
    if (this.dpsNetwork == null) {
      return false;
    }
    return this.dpsNetwork.shouldAutoConnect();
  }

  @Value
  @Builder
  public static class DpsNetwork {

    private Boolean autoCreate;
    private Boolean autoConnect;

    public boolean shouldAutoConnect() {
      return BooleanUtils.isTrue(this.autoConnect);
    }

    public boolean shouldAutoCreate() {
      return BooleanUtils.isTrue(this.autoCreate);
    }
  }
}
