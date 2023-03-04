package com.mageddo.dnsproxyserver.utils;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

public class Dns {

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

  public static void validateIsDefaultPort(IpAddr addr) {
    Validate.isTrue(
      Dns.isDefaultPortOrNull(addr),
      "Resolvconf requires dns server port to be=%s, passedPort=%d",
      Dns.DEFAULT_PORT, addr.getPort()
    );
  }
}
