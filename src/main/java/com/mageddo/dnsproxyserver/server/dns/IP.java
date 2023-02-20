package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.utils.Bytes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class IP {

  public static final int BYTES = 4;
  private final String ip;

  public IP(String ip) {
    this.ip = ip;
  }

  @Override
  public String toString() {
    return this.ip;
  }

  public String raw() {
    return this.ip;
  }

  public byte[] toByteArray() {
    return Ips.toBytes(this.raw());
  }

  public Short[] toShortArray() {
    return Bytes.toUnsignedShortArray(this.toByteArray());
  }

  public static IP of(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return new IP(ip);
  }

  public static IP of(Short[] ip) {
    return of(Bytes.toNative(ip));
  }

  public static IP of(byte[] data) {
    if (data == null) {
      return null;
    }
    Validate.isTrue(
      data.length == BYTES,
      "Array of bytes is not a valid IP representation, size must be %d",
      BYTES
    );
    return of(String.format(
      "%d.%d.%d.%d",
      Byte.toUnsignedInt(data[0]), Byte.toUnsignedInt(data[1]),
      Byte.toUnsignedInt(data[2]), Byte.toUnsignedInt(data[3])
    ));
  }

  public static Short[] toShortArray(String ip) {
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return IP.of(ip).toShortArray();
  }
}
