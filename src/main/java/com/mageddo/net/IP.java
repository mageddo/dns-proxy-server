package com.mageddo.net;

import java.net.InetAddress;

public interface IP {

  int IPV4_BYTES = 4;
  int IPV6_BYTES = 16;

  byte[] toByteArray();

  Short[] toShortArray();

  String toText();

  InetAddress toInetAddr();

  Version version();

  static IP of(String ip) {
    return IpImpl.of(ip);
  }

  static IP of(byte[] data) {
    return IpImpl.of(data);
  }

  boolean isLoopback();

  enum Version {

    IPV4,
    IPV6,
    ;

    public boolean isIpv6() {
      return this == IPV6;
    }
  }
}
