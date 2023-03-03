package com.mageddo.dnsproxyserver.server.dns;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Hostnames {

  public static final String HOSTNAME_TERMINATE_CHAR = ".";

  public static String toAbsoluteName(String s) {
    if (s.endsWith(HOSTNAME_TERMINATE_CHAR)) {
      return s;
    }
    return s + HOSTNAME_TERMINATE_CHAR;
  }

  public static boolean matches(Hostname expected, String actual) {
    return expected.isEqualTo(actual);
  }
}
