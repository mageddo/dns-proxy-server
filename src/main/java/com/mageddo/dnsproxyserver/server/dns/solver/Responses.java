package com.mageddo.dnsproxyserver.server.dns.solver;

public class Responses {
  public static boolean hasFlag(Response r, int flag) {
    if (r == null) {
      return false;
    }
    return r.getMessage().getHeader().getFlag(flag);
  }
}
