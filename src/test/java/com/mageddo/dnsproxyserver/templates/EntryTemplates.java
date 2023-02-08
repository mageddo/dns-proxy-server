package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.Config;

public class EntryTemplates {
  public static Config.Entry build(String host) {
    return Config.Entry
      .builder()
      .hostname(host)
      .ip("10.10.0.1")
      .ttl(45)
      .type(Config.Entry.Type.A)
      .build()
      ;
  }

  public static Config.Entry cname(String from, String to) {
    return Config.Entry
      .builder()
      .hostname(from)
      .target(to)
      .ttl(45)
      .type(Config.Entry.Type.CNAME)
      .build()
      ;
  }
}
