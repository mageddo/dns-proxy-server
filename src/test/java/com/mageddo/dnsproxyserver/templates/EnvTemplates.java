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
  public static Config.Env acmeQuadA(){
    return Config.Env.theDefault()
      .add(EntryTemplates.acmeQuadA());
  }

  public static Config.Env acmeA(){
    return Config.Env.theDefault()
      .add(EntryTemplates.acmeA());
  }

  public static Config.Env acmeAAndQuadA(){
    return Config.Env.theDefault()
      .add(EntryTemplates.acmeA())
      .add(EntryTemplates.acmeQuadA())
      ;
  }

  public static Config.Env acmeCname() {
    return Config.Env.theDefault()
      .add(EntryTemplates.acmeCname())
      ;
  }
}
