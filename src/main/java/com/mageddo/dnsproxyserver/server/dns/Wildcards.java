package com.mageddo.dnsproxyserver.server.dns;

import java.util.ArrayList;
import java.util.List;

public class Wildcards {
  public static List<Hostname> buildHostAndWildcards(Hostname hostname) {

    final var query = "." + hostname.getValue();
    final var hostnames = new ArrayList<Hostname>();
    hostnames.add(Hostname.of(query.substring(1)));

    int fromIndex = 0, actual = 0;
    while (true) {
      final var str = query.substring(fromIndex);
      actual = str.indexOf('.');

      if (actual == -1 || actual + 1 >= str.length()) {
        break;
      }
      hostnames.add(Hostname.of(str.substring(actual)));
      fromIndex += actual + 1;
    }
    return hostnames;
  }
}
