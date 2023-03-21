package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder
@EqualsAndHashCode
public class HostnameQuery {

  public static final String REGEX_TAG = "/";

  @NonNull
  private final Hostname hostname;

  @NonNull
  private final IP.Version version;

  private final boolean useWildcards;

  private final boolean useRegex;

  public static HostnameQuery of(Hostname hostname) {
    return of(hostname, false, false);
  }

  public static HostnameQuery ofWildcard(String hostname) {
    return ofWildcard(hostname, IP.Version.IPV4);
  }

  public static HostnameQuery ofWildcard(String hostname, IP.Version version) {
    return ofWildcard(Hostname.of(hostname), version);
  }

  public static HostnameQuery ofWildcard(Hostname hostname) {
    return ofWildcard(hostname, IP.Version.IPV4);
  }

  public static HostnameQuery ofWildcard(Hostname hostname, IP.Version version) {
    return builder()
      .useWildcards(true)
      .useRegex(false)
      .build();
  }

  public static HostnameQuery ofRegex(String hostname) {
    return ofRegex(hostname, IP.Version.IPV4);
  }

  public static HostnameQuery ofRegex(String hostname, IP.Version version) {
    return ofRegex(Hostname.of(hostname), version);
  }

  public static HostnameQuery ofRegex(Hostname hostname) {
    return ofRegex(hostname, IP.Version.IPV4);
  }

  public static HostnameQuery ofRegex(Hostname hostname, IP.Version version) {
    return builder()
      .useWildcards(false)
      .useRegex(true)
      .build();
  }

  public static HostnameQuery of(Hostname hostname, boolean wildcards, boolean regex) {
    return HostnameQuery
      .builder()
      .hostname(hostname)
      .useWildcards(wildcards)
      .useRegex(regex)
      .version(IP.Version.IPV4)
      .build();
  }

  public static HostnameQuery of(String hostname) {
    return of(Hostname.of(hostname));
  }

  public boolean matches(Hostname hostname) {
    return matches(hostname.getCanonicalValue());
  }

  public boolean matches(String hostnamePattern) {
    if (this.useWildcards) {
      final var hostnames = Wildcards.buildHostAndWildcards(this.hostname);
      for (final var host : hostnames) {
        if (host.isEqualTo(hostnamePattern)) {
          return true;
        }
      }
      return false;
    }
    if (this.useRegex && hostnamePattern.startsWith(REGEX_TAG) && hostnamePattern.endsWith(REGEX_TAG)) {
      return this.hostname
        .getCanonicalValue()
        .matches(StringUtils.substringBetween(hostnamePattern, REGEX_TAG, REGEX_TAG))
        ;
    }
    return this.hostname.isEqualTo(hostnamePattern);
  }
}
