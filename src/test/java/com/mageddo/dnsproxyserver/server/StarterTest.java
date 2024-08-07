package com.mageddo.dnsproxyserver.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StarterTest {

  final Starter starter = spy(new Starter(null, null, Set.of()));

  @Test
  void wontStartInTestMode() {

    doNothing().when(this.starter).startWebServer();
    doNothing().when(this.starter).startDnsServer();

    this.starter.start();

    verify(this.starter, never()).startDnsServer();
    verify(this.starter).startWebServer();
  }


  @Test
  void mustStartWhenInTestModeAndFlagForced() {

    Starter.setMustStartFlagActive(true);

    doNothing().when(this.starter).startWebServer();
    doNothing().when(this.starter).startDnsServer();

    this.starter.start();

    verify(this.starter).startDnsServer();
    verify(this.starter).startWebServer();

    Starter.setMustStartFlagActive(false);
  }
}
