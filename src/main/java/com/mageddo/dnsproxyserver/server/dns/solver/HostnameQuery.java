package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class HostnameQuery {

  @NonNull
  private final Hostname hostname;

  private final boolean useWildcards;

  private final boolean useRegex;

  public static HostnameQuery of(Hostname hostname) {
    return of(hostname, false, false);
  }

  public static HostnameQuery ofWildcard(String hostname) {
    return ofWildcard(Hostname.of(hostname));
  }

  public static HostnameQuery ofWildcard(Hostname hostname) {
    return of(hostname, true, false);
  }

  public static HostnameQuery ofRegex(String hostname) {
    return ofRegex(Hostname.of(hostname));
  }

  public static HostnameQuery ofRegex(Hostname hostname) {
    return of(hostname, false, true);
  }

  public static HostnameQuery of(Hostname hostname, boolean wildcards, boolean regex) {
    return HostnameQuery
      .builder()
      .hostname(hostname)
      .useWildcards(wildcards)
      .useRegex(regex)
      .build();
  }

  public static HostnameQuery of(String hostname) {
    return of(Hostname.of(hostname));
  }

  public boolean matches(Hostname hostname) {
    return matches(hostname.getValue());
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
    if (this.useRegex) {
      return this.hostname
        .getCanonicalValue()
        .matches(hostnamePattern)
        ;
    }
    return this.hostname.isEqualTo(hostnamePattern);
  }
}
