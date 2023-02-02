package com.mageddo.dnsproxyserver.server.dns;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@EqualsAndHashCode(of = "name")
public class Hostname {

  private final String name;

  public Hostname(String name) {
    this.name = StringUtils.lowerCase(name);
  }

  public boolean isEqualTo(String cname) {
    return this.isEqualTo(new Hostname(cname));
  }

  public boolean isEqualTo(Hostname hostname) {
    return Objects.equals(this.name, hostname.name);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public static Hostname of(String hostname){
    return new Hostname(hostname);
  }
}
