package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.Config;

public class EnvTemplates {
  public static Config.Env buildWithoutId(){
    return Config.Env.theDefault()
      .add(Config.Entry
        .builder()
        .ip("192.168.0.1")
        .ttl(30)
        .type(Config.Entry.Type.A)
        .hostname("mageddo.com")
        .build()
      );
  }
}
