package com.mageddo.dnsproxyserver.dnsconfigurator;

import java.util.List;

import com.mageddo.os.Platform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import testing.templates.IpAddrTemplates;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DnsConfiguratorDefaultTest {

  @Spy
  @InjectMocks
  DnsConfiguratorDefault configurator;

  @BeforeEach
  void beforeEach() {
    assumeTrue(Platform.isWindows() || Platform.isMac());
  }

  @Test
  void mustConfigureDNSServer() {
    // arrange
    final var addr = IpAddrTemplates.local();
    final var network = "WI-FI";

    doReturn(true)
        .when(this.configurator)
        .updateDnsServers(any(), any())
    ;

    doReturn(List.of("8.8.8.8"))
        .when(this.configurator)
        .findNetworkDnsServers(eq(network))
    ;

    doReturn(singletonList(network))
        .when(this.configurator)
        .findNetworks()
    ;

    // act
    this.configurator.configure(addr);

    // assert
    verify(this.configurator).updateDnsServers(eq(network), eq(singletonList(addr.getIpAsText())));
    assertEquals("{WI-FI=[8.8.8.8]}", this.configurator.getServersBefore()
        .toString()
    );
  }

  @Test
  void mustStoreServersBeforeOnceAndNotReplaceByOtherValues() {
    // arrange
    final var addr = IpAddrTemplates.local();
    final var network = "WI-FI";

    doReturn(true)
        .when(this.configurator)
        .updateDnsServers(any(), any())
    ;

    doReturn(List.of("8.8.8.8"))
        .when(this.configurator)
        .findNetworkDnsServers(eq(network))
    ;

    doReturn(singletonList(network))
        .when(this.configurator)
        .findNetworks()
    ;

    // act
    this.configurator.configure(addr);
    this.configurator.configure(addr);

    // assert
    verify(this.configurator, times(1)).updateDnsServers(anyString(), any());
    verify(this.configurator, times(1)).findNetworkDnsServers(any());
    assertEquals("{WI-FI=[8.8.8.8]}", this.configurator.getServersBefore()
        .toString()
    );
  }

}
