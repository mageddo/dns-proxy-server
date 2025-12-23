package com.mageddo.dnsproxyserver.solver.docker;

import com.mageddo.net.IP;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Entry {

  private boolean hostnameMatched;

  private IP ip;

  public String getIpText() {
    return this.ip != null ? this.ip.toText() : null;
  }

  public boolean isHostNameNotMatched() {
    return !this.hostnameMatched;
  }

  public boolean hasNotIP() {
    return this.ip == null;
  }

  public boolean hasIp() {
    return this.ip != null;
  }
}
