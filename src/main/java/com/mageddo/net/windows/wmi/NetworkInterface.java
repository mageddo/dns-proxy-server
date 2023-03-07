package com.mageddo.net.windows.wmi;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class NetworkInterface {

  @NonNull
  private String caption;

  @NonNull
  private String description;

  @NonNull
  private String id;

  @NonNull
  private List<String> dnsServers;

  @NonNull
  private Boolean dynamicDnsRegistration;

}
