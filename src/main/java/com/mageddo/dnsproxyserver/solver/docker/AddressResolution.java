package com.mageddo.dnsproxyserver.solver.docker;

import java.time.Duration;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddressResolution {

  boolean hostnameMatched;

  IP ip;

  Integer ttl;

  public String getIpText() {
    return this.ip != null ? this.ip.toText() : null;
  }

  public Duration getTTLDuration(Duration def) {
    if (this.ttl == null) {
      return def;
    }
    return Duration.ofSeconds(this.ttl);
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

  public static AddressResolution matched(IP ip) {
    return matched(ip, null);
  }

  public static AddressResolution matched(IP ip, Integer ttl) {
    return builder()
        .hostnameMatched(true)
        .ip(ip)
        .ttl(ttl)
        .build();
  }


  public static AddressResolution notMatched() {
    return builder()
        .hostnameMatched(false)
        .build();
  }

}
