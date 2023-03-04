package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.templates.IpAddrTemplates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DnsConfiguratorOSxTest {

  @Spy
  @InjectMocks
  DnsConfiguratorOSx configurator;

  @Captor
  ArgumentCaptor<List<String>> stringListCaptor;

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
    verify(this.configurator).updateDnsServers(eq(network), eq(singletonList(addr.getRawIP())));
    assertEquals("{WI-FI=[8.8.8.8]}", this.configurator.getServersBefore().toString());
  }

}
