package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.IP;
import com.mageddo.utils.Bytes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.net.InetAddress;

public class Ipv4 implements IP {

  private final String ip;
  private final Version version;

  public Ipv4(String ip) {
    this.ip = ip;
    this.version = Version.IPV4;
  }

  @Override
  public String toString() {
    return this.ip;
  }

  public String raw() {
    return this.ip;
  }

  @Override
  public byte[] toByteArray() {
    return Ips.toBytes(this.raw());
  }

  @Override
  public Short[] toShortArray() {
    return Bytes.toUnsignedShortArray(this.toByteArray());
  }

  @Override
  public String toText() {
    return this.raw();
  }

  @Override
  public InetAddress toInetAddr() {
    return Ips.toAddress(this);
  }

  @Override
  public Version version() {
    return this.version;
  }

  public static Ipv4 of(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return new Ipv4(ip);
  }

  public static Ipv4 of(Short[] ip) {
    return of(Bytes.toNative(ip));
  }

  public static Ipv4 of(byte[] data) {
    if (data == null) {
      return null;
    }
    Validate.isTrue(
      data.length == IPV4_BYTES,
      "Array of bytes is not a valid IP representation, size must be %d",
      IPV4_BYTES
    );
    return of(String.format(
      "%d.%d.%d.%d",
      Byte.toUnsignedInt(data[0]), Byte.toUnsignedInt(data[1]),
      Byte.toUnsignedInt(data[2]), Byte.toUnsignedInt(data[3])
    ));
  }

  public boolean isLoopback() {
    return this.ip.startsWith("127.");
  }

}
