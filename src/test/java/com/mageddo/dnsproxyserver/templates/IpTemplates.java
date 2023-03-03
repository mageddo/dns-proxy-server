package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.IP;

public class IpTemplates {

  public static final String LOCAL = "10.10.0.1";

  public static IP local(){
    return IP.of(LOCAL);
  }

  public static IP loopback(){
    return IP.of("127.0.0.1");
  }
}
