package com.mageddo.dnsproxyserver.utils;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import com.mageddo.commons.regex.Regexes;
import com.mageddo.net.IP;
import com.mageddo.utils.Bytes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Ips {

  private static final Pattern IPV4_REGEX = Pattern.compile(
      "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$"
  );

  private Ips() {
  }

  public static byte[] toBytes(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    final var tokens = ip.split("\\.");
    Validate.isTrue(tokens.length == 4, "Wrong number of tokens: %d, ip*=%s", tokens.length, ip);
    final var bytes = new byte[4];
    for (int i = 0; i < tokens.length; i++) {
      bytes[i] = (byte) Integer.parseInt(tokens[i]);
    }
    return bytes;
  }

  public static InetAddress toAddress(String ip) {
    try {
      if (StringUtils.isBlank(ip)) {
        return null;
      }
      return InetAddress.getByName(ip);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  public static InetAddress toAddress(byte[] ip) {
    try {
      return InetAddress.getByAddress(ip);
    } catch (UnknownHostException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static InetAddress toAddress(IP ip) {
    return toAddress(ip.toText());
  }

  public static InetSocketAddress toSocketAddress(String ip, int port) {
    return new InetSocketAddress(Ips.toAddress(ip), port);
  }

  public static InetAddress getAnyLocalAddress() {
    try {
      return InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
    } catch (UnknownHostException e) {
      return null;
    }
  }

  public static InetSocketAddress getAnyLocalAddress(int port) {
    return new InetSocketAddress(getAnyLocalAddress(), port);
  }

  public static InetSocketAddress getAnyLocalIpv6Address(int port) {
    return new InetSocketAddress(getAnyLocalIpv6Address(), port);
  }

  public static InetAddress getAnyLocalIpv6Address() {
    try {
      return InetAddress.getByAddress(new byte[16]);
    } catch (UnknownHostException e) {
      return null;
    }
  }

  public static boolean isIpv6(String v) {
    return StringUtils.trimToEmpty(v)
        .contains(":");
  }

  public static boolean isIpv4(String v) {
    return Regexes.matches(v, IPV4_REGEX);
  }

  public static Short[] toShortArray(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return IP.of(ip)
        .toShortArray();
  }

  public static IP toIp(Short[] ip) {
    return IP.of(Bytes.toNative(ip));
  }

  public static IP from(InetAddress address) {
    return IP.of(address.getAddress());
  }
}
