package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Config.Entry.Type;

public class EntryTemplates {

  public static final String ACME_COM = "acme.com";

  public static Config.Entry a(String host) {
    return Config.Entry
      .builder()
      .hostname(host)
      .ip("10.10.0.1")
      .ttl(45)
      .type(Type.A)
      .build()
      ;
  }

  public static Config.Entry cname(String from, String to) {
    return Config.Entry
      .builder()
      .hostname(from)
      .target(to)
      .ttl(45)
      .type(Type.CNAME)
      .build()
      ;
  }

  public static Config.Entry aaaa(String host) {
    return a(host)
      .toBuilder()
      .type(Type.AAAA)
      .build();
  }

  public static Config.Entry acmeAAAA() {
    return aaaa(ACME_COM);
  }
}
