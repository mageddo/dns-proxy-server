package com.mageddo.dnsproxyserver.docker;

import com.mageddo.net.IP;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Entry {

  private boolean hostnameMatched;

  private IP ip;

  public String getIpIfVersionMatches(IP.Version version) {
    return this.ip != null && this.ip.version() == version ? this.ip.toText() : null;
  }
}
