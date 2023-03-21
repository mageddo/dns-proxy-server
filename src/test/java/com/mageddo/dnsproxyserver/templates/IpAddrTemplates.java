package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.IpSockAddr;

public class IpAddrTemplates {

  public static IpSockAddr local() {
    return IpSockAddr.of("10.10.0.1");
  }

  public static IpSockAddr localPort54() {
    return IpSockAddr.of("10.10.0.1:54");
  }

  public static IpSockAddr loopback() {
    return IpSockAddr.of(IpTemplates.loopback());
  }
}
