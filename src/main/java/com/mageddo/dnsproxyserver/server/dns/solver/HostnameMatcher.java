package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.server.dns.Wildcards;

import java.util.function.Function;

public class HostnameMatcher {

  public static <T> T match(Hostname hostname, Function<Hostname, T> hostnameProviderFn) {
    for (final var host : Wildcards.buildHostAndWildcards(hostname)) {
      final var ip = hostnameProviderFn.apply(host);
      if (ip != null) {
        return ip;
      }
    }
    return null;
  }

}
