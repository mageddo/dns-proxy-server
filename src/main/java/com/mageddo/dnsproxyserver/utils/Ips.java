package com.mageddo.dnsproxyserver.utils;

import org.apache.commons.lang3.Validate;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ips {
  public static byte[] toBytes(String ip) {
    final var tokens = ip.split("\\.");
    Validate.isTrue(tokens.length == 4, "Wrong number of tokens: %d, ip*=%s", tokens.length, ip);
    final var bytes = new byte[4];
    for (int i = 0; i < tokens.length; i++) {
      bytes[i] = (byte)Integer.parseInt(tokens[i]);
    }
    return bytes;
  }

  public static InetAddress toAddress(String ip) {
    return toAddress(toBytes(ip));
  }

  public static InetAddress toAddress(byte[] ip) {
    try {
      return InetAddress.getByAddress(ip);
    } catch (UnknownHostException e) {
      throw new UncheckedIOException(e);
    }
  }
}
