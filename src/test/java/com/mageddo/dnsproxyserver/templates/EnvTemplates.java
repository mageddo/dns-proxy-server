package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.EntryType;

public class EnvTemplates {
  public static Config.Env buildWithoutId(){
    return Config.Env.theDefault()
      .add(Config.Entry
        .builder()
        .ip("192.168.0.1")
        .ttl(30)
        .type(EntryType.A)
        .hostname("mageddo.com")
        .build()
      );
  }
}
