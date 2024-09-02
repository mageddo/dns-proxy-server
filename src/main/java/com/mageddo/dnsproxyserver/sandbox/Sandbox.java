package com.mageddo.dnsproxyserver.sandbox;

public class Sandbox {
  public static Instance runFromGradleTests(String[] args) {
    return new BinaryFromGradleTestsSandbox().run(args);
  }
}
