package com.mageddo.dnsproxyserver.templates;

import lombok.SneakyThrows;

import java.net.InetAddress;

public class InetAddressTemplates {

  @SneakyThrows
  public static InetAddress loopback() {
    return InetAddress.getByName("127.0.0.1");
  }

  @SneakyThrows
  public static InetAddress local() {
    return InetAddress.getByName(IpTemplates.LOCAL);
  }

  @SneakyThrows
  public static InetAddress local192() {
    return InetAddress.getByName(IpTemplates.LOCAL_192);
  }
}
