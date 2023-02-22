package com.mageddo.dnsproxyserver.docker.domain;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

public enum Network {

  DPS,
  BRIDGE,
  HOST,
  OTHER;

  public static Network of(String name) {
    return EnumUtils.getEnumIgnoreCase(Network.class, name, OTHER);
  }

  public String lowerCaseName() {
    return StringUtils.lowerCase(this.name());
  }
}
