package com.mageddo.net;

import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.utils.Bytes;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

class IpImpl implements IP {

  private final InetAddress ip;
  private final Version version;

  IpImpl(String ip) {
    this(build(ip));
  }

  public IpImpl(InetAddress ip) {
    this.ip = ip;
    this.version = this.ip.getAddress().length == IP.IPV4_BYTES ? Version.IPV4 : Version.IPV6;
  }

  @Override
  public String toString() {
    return this.ip.getHostAddress();
  }

  @Override
  public byte[] toByteArray() {
    return this.ip.getAddress();
  }

  @Override
  public Short[] toShortArray() {
    return Bytes.toUnsignedShortArray(this.toByteArray());
  }

  @Override
  public String toText() {
    return this.ip.getHostAddress();
  }

  @Override
  public InetAddress toInetAddr() {
    return Ips.toAddress(this);
  }

  @Override
  public Version version() {
    return this.version;
  }

  public static IpImpl of(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return new IpImpl(ip);
  }

  public static IpImpl of(Short[] ip) {
    return of(Bytes.toNative(ip));
  }

  public static IpImpl of(byte[] data) {
    if (data == null) {
      return null;
    }
    try {
      return new IpImpl(InetAddress.getByAddress(data));
    } catch (UnknownHostException e) {
      throw invalidAddressEx(e);
    }
  }

  @Override
  public boolean isLoopback() {
    return this.ip.isLoopbackAddress();
  }

  private static InetAddress build(String ip) {
    try {
      return InetAddress.getByName(ip);
    } catch (UnknownHostException e) {
      throw invalidAddressEx(e);
    }
  }

  static RuntimeException invalidAddressEx(UnknownHostException e) {
    return new RuntimeException(
      String.format("Array of bytes is not a valid IP representation: %s", e.getMessage()),
      e
    );
  }
}
