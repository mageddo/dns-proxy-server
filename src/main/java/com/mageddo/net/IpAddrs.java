package com.mageddo.net;

import java.net.InetSocketAddress;

import com.mageddo.dnsproxyserver.utils.Ips;

public class IpAddrs {
  public static IpAddr from(InetSocketAddress inetSocketAddress) {
    return IpAddr.of(
        Ips.from(inetSocketAddress.getAddress()),
        inetSocketAddress.getPort()
    );
  }

  public static InetSocketAddress toInetSocketAddress(IpAddr ipAddr) {
    return new InetSocketAddress(ipAddr.getIp()
        .toInetAddr(), ipAddr.getPort()
    );
  }

}
