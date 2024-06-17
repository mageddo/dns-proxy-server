package com.mageddo.net;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mageddo.dnsproxyserver.json.converter.IPConverter;
import com.mageddo.utils.Bytes;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class IpAddr {

  @NonNull
  @JsonDeserialize(using = IPConverter.Deserializer.class)
  @JsonSerialize(using = IPConverter.Serializer.class)
  private IP ip;

  private Integer port;

  public int getPortOrDef(int def) {
    return this.port == null ? def : this.port;
  }

  @Override
  public String toString() {
    if (this.port == null) {
      return this.getRawIP();
    }
    return String.format("%s:%d", this.ip, this.port);
  }

  public String getRawIP() {
    return this.ip.toText();
  }

  /***
   *
   * @param addr something like 192.168.0.1 or 192.168.0.1:4411
   * @return parsed object.
   */
  public static IpAddr of(String addr) {
    return IpUtils.toIpAddr(addr);
  }

  public static IpAddr of(IP ip) {
    return of(ip, null);
  }

  public static IpAddr of(IP ip, Integer port) {
    return IpAddr
      .builder()
      .ip(ip)
      .port(port)
      .build();
  }

  public static IpAddr of(Byte[] ip) {
    return of(Bytes.toNative(ip));
  }

  public static IpAddr of(byte[] ip) {
    return IpAddr.of(IpImpl.of(ip));
  }

  public static IpAddr of(Integer[] ip) {
    return of(Bytes.toNative(ip));
  }

  public boolean hasPort() {
    return this.port != null && this.port > 0;
  }

}
