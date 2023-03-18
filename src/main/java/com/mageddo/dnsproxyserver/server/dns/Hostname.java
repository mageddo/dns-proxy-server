package com.mageddo.dnsproxyserver.server.dns;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Case insensitive for comparison but will keep the original value stored when
 * it needs to be restored.
 */
@EqualsAndHashCode(of = "value")
public class Hostname {

  private final String value;
  private final String rawValue;

  public Hostname(String value) {
    this.value = StringUtils.lowerCase(value);
    this.rawValue = value;
  }

  public boolean isEqualTo(String cname) {
    return this.isEqualTo(of(cname));
  }

  public boolean isEqualTo(Hostname hostname) {
    return Objects.equals(this.value, hostname.value);
  }

  public String getCanonicalValue() {
    return this.value;
  }

  public String getValue() {
    return this.rawValue;
  }

  @Override
  public String toString() {
    return this.rawValue;
  }

  public static Hostname of(String hostname) {
    return new Hostname(hostname);
  }
}
