package com.mageddo.dnsproxyserver.net;

import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.SneakyThrows;

import java.io.UncheckedIOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Comparator;
import java.util.stream.Stream;

public class Networks {

  @SneakyThrows
  public static IP findCurrentMachineIP() {
    return findMachineIps()
      .findFirst()
      .orElse(null);
  }

  public static Stream<IP> findMachineIps() {
    try {
      return NetworkInterface
        .networkInterfaces()
        .flatMap(NetworkInterface::inetAddresses)
        .filter(it -> it.getAddress().length == IP.BYTES)
        .filter(it -> !it.isLoopbackAddress())
        .map(it -> IP.of(it.getHostAddress()))
        .sorted(Comparator.comparing(it -> it.raw().startsWith("127") ? Integer.MAX_VALUE : 0))
        ;
    } catch (SocketException e) {
      throw new UncheckedIOException(e);
    }
  }
}
