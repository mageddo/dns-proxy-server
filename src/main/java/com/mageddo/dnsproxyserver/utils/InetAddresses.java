package com.mageddo.dnsproxyserver.utils;

import com.mageddo.dnsproxyserver.server.dns.IpSockAddr;

import java.net.InetSocketAddress;

public class InetAddresses {

  public static InetSocketAddress toSocketAddress(String ip, int port) {
    return new InetSocketAddress(Ips.toAddress(ip), port);
  }

  public static InetSocketAddress toSocketAddress(IpSockAddr dns) {
    return new InetSocketAddress(Ips.toAddress(dns.getIp().raw()), dns.getPort());
  }
}
