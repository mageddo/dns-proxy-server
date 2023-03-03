package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;

public class IpAddrTemplates {

  public static IpAddr local() {
    return IpAddr.of("10.10.0.1");
  }

  public static IpAddr localPort54() {
    return IpAddr.of("10.10.0.1:54");
  }

  public static IpAddr loopback() {
    return IpAddr.of(IpTemplates.loopback());
  }
}
