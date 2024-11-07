package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.net.IpAddr;

public class HostnameIpExtractor {
  public static IpAddr extract(String hostname, String domain) {
    hostname = removeDomainFrom(hostname, domain);
    return IpAddr.of("192.168.0.1");
  }

  static String removeDomainFrom(String hostname, String domain) {
    final var idx = hostname.indexOf(domain);
    if (idx < 0) {
      return hostname;
    }
    return hostname.substring(0, idx - 1);
  }

//  static String findValidSeparators(String str){
//    final var groups = Regexes.groups(str, "([-_\\.]+)");
//    groups.size()
//  }
}
