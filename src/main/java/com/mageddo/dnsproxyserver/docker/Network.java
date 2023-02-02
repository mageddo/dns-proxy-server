package com.mageddo.dnsproxyserver.docker;

import org.apache.commons.lang3.EnumUtils;

public enum Network {

  DPS,
  BRIDGE,
  OTHER;

  public static Network of(String name) {
    return EnumUtils.getEnumIgnoreCase(Network.class, name, OTHER);
  }

}
