package com.mageddo.dnsproxyserver.server.dns;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@EqualsAndHashCode(of = "value")
public class Hostname {

  private final String value;

  public Hostname(String value) {
    this.value = StringUtils.lowerCase(value);
  }

  public boolean isEqualTo(String cname) {
    return this.isEqualTo(new Hostname(cname));
  }

  public boolean isEqualTo(Hostname hostname) {
    return Objects.equals(this.value, hostname.value);
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }

  public static Hostname of(String hostname){
    return new Hostname(hostname);
  }
}
