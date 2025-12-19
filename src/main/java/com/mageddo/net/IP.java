package com.mageddo.net;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Stream;

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

  static List<IP> listOf(String... ips) {
    return Stream.of(ips)
        .map(IP::of)
        .toList();
  }

  boolean isLoopback();

  boolean isAnyLocal();

  boolean notEqualTo(String ip);

  default boolean versionIs(Version version) {
    return this.version()
        .equals(version);
  }

  enum Version {

    IPV4,
    IPV6,
    ;

    public boolean isIpv6() {
      return this == IPV6;
    }

    public boolean isIpv4() {
      return this == IPV4;
    }
  }
}
