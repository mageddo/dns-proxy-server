package com.mageddo.dnsproxyserver.solver;

import java.util.List;
import java.util.function.Function;

import com.mageddo.dns.Hostname;
import com.mageddo.net.IP;

public class HostnameEvaluator {

  public static <T> T eval(
      Hostname hostname, IP.Version version, Function<HostnameQuery, T> providerFn
  ) {

    final var wildcardHostname = HostnameQuery.ofWildcard(hostname, version);
    final var regexHostname = HostnameQuery.ofRegex(hostname, version);
    final var queries = List.of(wildcardHostname, regexHostname);

    for (final var host : queries) {
      final var ip = providerFn.apply(host);
      if (ip != null) {
        return ip;
      }
    }

    return null;
  }

}
