package com.mageddo.dnsproxyserver.sandbox;

import java.nio.file.Path;

public class Sandbox {
  public static Instance runFromGradleTests(Path configFile) {
    return new BinaryFromGradleTestsSandbox().run(configFile);
  }
}
