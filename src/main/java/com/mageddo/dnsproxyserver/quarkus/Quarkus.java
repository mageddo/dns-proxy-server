package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.utils.Tests;

public class Quarkus {
  public static boolean isTest() {
    return Tests.inTest();
  }
}
