package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Hostname;

import java.util.List;
import java.util.function.Function;

public class HostnameMatcher {

  public static <T> T match(Hostname hostname, Function<HostnameQuery, T> hostnameProviderFn) {

    final var wildcardHostname = HostnameQuery.ofWildcard(hostname);
    final var regexHostname = HostnameQuery.ofRegex(hostname);
    final var queries = List.of(wildcardHostname, regexHostname);

    for (final var host : queries) {
      final var ip = hostnameProviderFn.apply(host);
      if (ip != null) {
        return ip;
      }
    }

    return null;
  }

}
