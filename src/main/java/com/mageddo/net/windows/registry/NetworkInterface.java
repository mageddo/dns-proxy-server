package com.mageddo.net.windows.registry;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class NetworkInterface {

  @NonNull
  private String id;

  private String staticIp;
  private String dhcpIp;

  @NonNull
  private List<String> staticDnsServers;

  public String getIp() {
    return StringUtils.firstNonBlank(this.staticIp, this.dhcpIp);
  }

  public boolean hasIp() {
    return StringUtils.isNotBlank(this.getIp());
  }
}
