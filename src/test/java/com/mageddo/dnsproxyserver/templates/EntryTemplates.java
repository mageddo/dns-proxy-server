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
      .ip(IpTemplates.LOCAL_IPV6)
      .build();
  }

  public static Config.Entry acmeQuadA() {
    return aaaa(ACME_COM);
  }

  public static Config.Entry acmeA() {
    return a(ACME_COM);
  }

  public static Config.Entry acmeCname() {
    return cname(ACME_COM, HostnameTemplates.ORANGE_ACME_HOSTNAME);
  }
}
