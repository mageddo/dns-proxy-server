package com.mageddo.dnsproxyserver.net;

import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.SneakyThrows;

import java.net.NetworkInterface;

public class Networks {

  @SneakyThrows
  public static IP findCurrentMachineIP() {
    return NetworkInterface
      .networkInterfaces()
      .flatMap(NetworkInterface::inetAddresses)
      .filter(it -> it.getAddress().length == IP.BYTES)
      .map(it -> IP.of(it.getHostAddress()))
      .filter(it -> !it.raw().startsWith("127"))
      .findFirst()
      .orElse(null);
  }

}
