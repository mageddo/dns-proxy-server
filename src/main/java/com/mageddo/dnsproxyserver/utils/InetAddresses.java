package com.mageddo.dnsproxyserver.utils;

import com.mageddo.net.IpAddr;

import java.net.InetSocketAddress;

public class InetAddresses {

  public static InetSocketAddress toSocketAddress(String ip, int port) {
    return new InetSocketAddress(Ips.toAddress(ip), port);
  }

  public static InetSocketAddress toSocketAddress(IpAddr dns) {
    return new InetSocketAddress(Ips.toAddress(dns.getIp().toText()), dns.getPort());
  }
}
