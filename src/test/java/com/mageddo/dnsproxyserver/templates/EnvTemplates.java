package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.config.Config;

public class EnvTemplates {

  public static final String MAGEDDO_COM = "mageddo.com";
  public static final String MAGEDDO_COM_CAMEL_CASE = "mAgeDdo.cOm";

  public static Config.Env buildWithoutId(){
    return Config.Env.theDefault()
      .add(Config.Entry
        .builder()
        .ip("192.168.0.1")
        .ttl(30)
        .type(Config.Entry.Type.A)
        .hostname(MAGEDDO_COM)
        .build()
      );
  }

  public static Config.Env buildWithCamelCaseHost(){
    return Config.Env.theDefault()
      .add(Config.Entry
        .builder()
        .ip("192.168.0.1")
        .ttl(30)
        .type(Config.Entry.Type.A)
        .hostname(MAGEDDO_COM_CAMEL_CASE)
        .build()
      );
  }
}
