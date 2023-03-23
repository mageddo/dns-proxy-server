package com.mageddo.dnsproxyserver.templates;

import com.mageddo.net.IPI;

public class IpTemplates {

  public static final String LOCAL = "10.10.0.1";
  public static final String LOCAL_IPV6 = "2001:db8:1::2";
  public static final String LOCAL_EXTENDED_IPV6 = "2001:db8:1:0:0:0:0:2";

  public static IPI local(){
    return IPI.of(LOCAL);
  }

  public static IPI loopback(){
    return IPI.of("127.0.0.1");
  }
}
