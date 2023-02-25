package com.mageddo.dnsproxyserver.systemd;

import org.apache.commons.exec.OS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ResolvedServiceTest {

  @BeforeEach
  void beforeEach(){
    assumeTrue(OS.isFamilyUnix() && !OS.isFamilyMac());
  }

  @Test
  void mustRestartResolved(){

    // arrange

    // act
    ResolvedService.restart();

    // assert
  }
}
