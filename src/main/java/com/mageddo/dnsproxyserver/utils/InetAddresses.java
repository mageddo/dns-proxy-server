package com.mageddo.dnsproxyserver.utils;

import java.net.InetSocketAddress;

import com.mageddo.net.IP;
import com.mageddo.net.IpAddr;

public class InetAddresses {

  public static InetSocketAddress toSocketAddress(IP ip, int port) {
    return new InetSocketAddress(Ips.toAddress(ip), port);
  }

  public static InetSocketAddress toSocketAddress(String ip, int port) {
    return new InetSocketAddress(Ips.toAddress(ip), port);
  }

  public static InetSocketAddress toSocketAddress(IpAddr dns) {
    return new InetSocketAddress(Ips.toAddress(dns.getIp()
        .toText()), dns.getPort()
    );
  }
}
