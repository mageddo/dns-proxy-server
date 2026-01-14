package com.mageddo.dnsproxyserver.solver;

import java.time.Duration;
import java.util.List;

import com.mageddo.commons.Collections;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import static java.util.Objects.requireNonNullElse;

@Value
@Builder
public class AddressResolution {

  boolean hostnameMatched;

  @Singular
  List<IP> ips;

  Duration ttl;

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
    return !this.hasIp();
  }

  public boolean hasIp() {
    return Collections.isNotEmpty(this.ips);
  }

  @SuppressWarnings("unchecked")
  public List<String> getIps(Config.Entry.Type type) {
    final var version = type.toVersion();
    return IpMapper.toText(this.ips, version);
  }

  public static AddressResolution matched(IP ip) {
    return matched(ip, (Duration) null);
  }

  public static AddressResolution matched(IP ip, Integer ttl) {
    return matched(ip, Duration.ofSeconds(ttl));
  }

  public static AddressResolution matched(IP ip, Duration ttl) {
    if (ip == null) {
      return builder()
          .hostnameMatched(true)
          .ttl(ttl)
          .build();
    }
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

  public String firstAsText() {
    return IpMapper.toText(Collections.first(this.ips));
  }
}
