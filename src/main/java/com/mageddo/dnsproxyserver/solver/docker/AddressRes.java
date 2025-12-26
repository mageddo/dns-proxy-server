package com.mageddo.dnsproxyserver.solver.docker;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddressRes {

  boolean hostnameMatched;

  IP ip;

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

  public String getIp(Config.Entry.Type type) {
    final var version = type.toVersion();
    if (this.hasNotIP() || version == null || this.ip.versionIs(version)) {
      return this.getIpText();
    }
    return null;
  }

  public static AddressRes matched(IP ip) {
    return builder()
        .hostnameMatched(true)
        .ip(ip)
        .build();
  }

  public static AddressRes notMatched() {
    return builder()
        .hostnameMatched(false)
        .build();
  }

}
