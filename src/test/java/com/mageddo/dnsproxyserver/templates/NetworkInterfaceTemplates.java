package com.mageddo.dnsproxyserver.templates;

import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.mockito.quality.Strictness;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;

public class NetworkInterfaceTemplates {

  public static Stream<NetworkInterface> localAndLoopback() {
    final var loopback = buildNetworkInterface(true, 0, InetAddressTemplates.loopback());
    final var local10 = buildNetworkInterface(false, 1, InetAddressTemplates.local());
    final var local192 = buildNetworkInterface(false, 1, InetAddressTemplates.local192());
    return Stream.of(loopback, local10, local192);
  }

  @SneakyThrows
  static NetworkInterface buildNetworkInterface(
    final boolean isLoopback,
    final int index,
    final InetAddress address
  ) {
    final var loopback = Mockito.mock(
      NetworkInterface.class,
      Mockito
        .withSettings()
        .strictness(Strictness.LENIENT)
    );
    doReturn(isLoopback)
      .when(loopback)
      .isLoopback()
    ;

    doReturn(index)
      .when(loopback)
      .getIndex()
    ;

    doReturn(Stream.of(address))
      .when(loopback)
      .inetAddresses()
    ;

    doReturn(true)
      .when(loopback)
      .isUp()
    ;
    return loopback;
  }
}
