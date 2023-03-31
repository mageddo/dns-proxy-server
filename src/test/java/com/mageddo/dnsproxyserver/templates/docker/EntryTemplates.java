package com.mageddo.dnsproxyserver.templates.docker;

import com.mageddo.dnsproxyserver.docker.Entry;
import com.mageddo.dnsproxyserver.templates.IpTemplates;
import com.mageddo.net.IP;

public class EntryTemplates {
  public static Entry zeroIp() {
    return Entry
      .builder()
      .ip(IP.of(IpTemplates.ZERO))
      .hostnameMatched(true)
      .build();
  }

  public static Entry localIpv6() {
    return Entry
      .builder()
      .hostnameMatched(true)
      .ip(IP.of(IpTemplates.LOCAL_EXTENDED_IPV6))
      .build();
  }

  public static Entry hostnameMatchedButNoAddress() {
    return Entry
      .builder()
      .hostnameMatched(true)
      .build()
      ;
  }
}
