package com.mageddo.dnsproxyserver.docker.domain;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

public enum Network {

  DPS,
  BRIDGE,
  OTHER;

  public static Network of(String name) {
    return EnumUtils.getEnumIgnoreCase(Network.class, name, OTHER);
  }

  public String lowerName() {
    return StringUtils.lowerCase(this.name());
  }
}
