package com.mageddo.dnsproxyserver.solver;

import java.time.Duration;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;

import lombok.Builder;
import lombok.Value;

import static java.util.Objects.requireNonNullElse;

@Value
@Builder
public class AddressResolution {

  boolean hostnameMatched;

  IP ip;

  Duration ttl;

  public String getIpText() {
    return this.ip != null ? this.ip.toText() : null;
  }

  public Duration getTTL(Duration def) {
    return requireNonNullElse(this.ttl, def);
  }

  public Long getTTLAsSeconds() {
    return Objects.mapOrNull(this.ttl, Duration::toSeconds);
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
    return matched(ip, (Duration) null);
  }

  public static AddressResolution matched(IP ip, Integer ttl) {
    return matched(ip, Duration.ofSeconds(ttl));
  }

  public static AddressResolution matched(IP ip, Duration ttl) {
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
