package com.mageddo.dnsproxyserver.server.dns;

public class IP {

  private final String ip;

  public IP(String ip) {
    this.ip = ip;
  }

  public static IP of(String ip) {
    return new IP(ip);
  }

  public String raw() {
    return this.ip;
  }
}
