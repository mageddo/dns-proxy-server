package com.mageddo.dnsproxyserver.server.dns;

import org.apache.commons.lang3.Validate;

public class IP {

  public static final int IP_BYTES = 4;
  private final String ip;

  public IP(String ip) {
    this.ip = ip;
  }

  public static IP of(String ip) {
    return new IP(ip);
  }

  public static IP of(byte[] data) {
    Validate.isTrue(
      data.length == IP_BYTES,
      "Array of bytes is not a valid IP representation, size must be %d",
      IP_BYTES
    );
    return of(String.format("%d.%d.%d.%d", data[0], data[1], data[2], data[3]));
  }

  public String raw() {
    return this.ip;
  }
}
