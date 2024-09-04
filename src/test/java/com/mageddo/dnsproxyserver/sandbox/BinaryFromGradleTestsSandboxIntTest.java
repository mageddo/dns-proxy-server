package com.mageddo.dnsproxyserver.sandbox;

import org.junit.jupiter.api.Test;

class BinaryFromGradleTestsSandboxIntTest {

  BinaryFromGradleTestsSandbox sandbox = new BinaryFromGradleTestsSandbox();

  @Test
  void mustRunFromGradleTests(){
    // arrange
    final var args = new String[]{};

    // act
    this.sandbox.run(args);

    // assert
  }
}
