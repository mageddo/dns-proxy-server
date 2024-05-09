package com.mageddo.dnsproxyserver.server.dns.solver.docker;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder
public class Network {

  private String name;
  private String driver;

  public enum Priority {

    DPS,
    BRIDGE,
    HOST,
    OTHER;

    public static Priority of(String name) {
      return EnumUtils.getEnumIgnoreCase(Priority.class, name, OTHER);
    }

    public String lowerCaseName() {
      return StringUtils.lowerCase(this.name());
    }
  }
}
