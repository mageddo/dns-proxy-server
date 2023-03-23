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
    throw new UnsupportedOperationException();
  }

  static IP of(byte[] data) {
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
