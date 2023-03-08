package com.mageddo.net.windows.registry;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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
