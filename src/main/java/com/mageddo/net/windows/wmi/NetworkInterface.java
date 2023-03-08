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

  /**
   * DNS servers configured, can be servers obtained by the Network DHCP or set manually.
   */
  @NonNull
  private List<String> dnsServers;

  /**
   * Filled when manually dns are configured.
   */
  @NonNull
  private List<String> staticDnsServers;

}
