package com.mageddo.dnsproxyserver.utils;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;

import java.net.InetSocketAddress;

public class InetAddresses {

  public static InetSocketAddress toSocketAddress(IpAddr dns) {
    return new InetSocketAddress(Ips.toAddress(dns.getIp().raw()), dns.getPort());
  }
}
