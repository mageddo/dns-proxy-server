package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class HostnameQuery {

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

  public boolean matches(String hostname) {
    throw new UnsupportedOperationException();
  }
}
