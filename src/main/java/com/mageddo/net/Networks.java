package com.mageddo.net;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.SneakyThrows;

import java.io.UncheckedIOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Networks {

  @SneakyThrows
  public static IP findCurrentMachineIP() {
    return findMachineIps()
      .stream()
      .findFirst()
      .orElse(null);
  }

  public static List<IP> findMachineIps() {
    return findInterfaces()
      .stream()
      .flatMap(NetworkInterface::inetAddresses)
      .filter(it -> it.getAddress().length == IP.BYTES)
      .map(it -> IP.of(it.getHostAddress()))
      .sorted(Comparator.comparing(it -> it.raw().startsWith("127") ? Integer.MAX_VALUE : 0))
      .toList()
      ;
  }

  public static List<NetworkInterface> findInterfaces() {
    try {
      return NetworkInterface
        .networkInterfaces()
        .filter(it -> {
          try {
            return it.isUp();
          } catch (SocketException e) {
            return false;
          }
        })
        .toList();
    } catch (SocketException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String findIpv4Address(String networkName, Container container) {
    final var containerNetwork = findContainerNetwork(networkName, container);
    if (containerNetwork == null) {
      return null;
    }
    return containerNetwork.getIpAddress();
  }

  public static ContainerNetwork findContainerNetwork(String networkName, Container container) {
    final var settings = container.getNetworkSettings();
    if (settings == null) {
      return null;
    }
    return settings
      .getNetworks()
      .get(networkName);
  }

  public static String findIpv4Address(ContainerNetwork containerNetwork) {
    return Optional
      .ofNullable(containerNetwork)
      .map(ContainerNetwork::getIpAddress)
      .orElse(null);
  }

}
