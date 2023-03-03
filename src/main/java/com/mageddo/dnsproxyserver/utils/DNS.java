package com.mageddo.dnsproxyserver.utils;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;

import java.util.Objects;

public class DNS {

  public static int DEFAULT_PORT = 53;

  public static boolean isDefaultPort(IpAddr addr) {
    return isDefaultPort(addr.getPort());
  }

  public static boolean isDefaultPort(Integer port) {
    return Objects.equals(DEFAULT_PORT, port);
  }

  public static boolean isDefaultPortOrNull(IpAddr addr) {
    return isDefaultPort(addr) || !addr.hasPort();
  }
}
