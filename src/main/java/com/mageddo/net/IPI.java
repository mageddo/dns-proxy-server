package com.mageddo.net;

import java.net.InetAddress;

public interface IPI {

  int IPV4_BYTES = 4;
  int IPV6_BYTES = 16;

  byte[] toByteArray();

  Short[] toShortArray();

  String toText();

  InetAddress toInetAddr();

  static IPI of(String ip) {
    throw new UnsupportedOperationException();
  }

  static IPI of(byte[] data) {
    throw new UnsupportedOperationException();
  }

  enum Version {

    IPV4,
    IPV6,
    ;

    public boolean isIpv6() {
      return this == IPV6;
    }
  }
}
