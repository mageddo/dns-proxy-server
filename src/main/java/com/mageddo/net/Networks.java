package com.mageddo.net;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.SneakyThrows;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Networks {

  volatile static Network network = Network.getInstance();

  @SneakyThrows
  public static IP findCurrentMachineIP() {
    return findMachineIps()
      .stream()
      .findFirst()
      .orElse(null);
  }

  /**
   *  The "relevance" is understood as the IP which have most chances of represent the real hardware network interface,
   *  we say "most chances" beucase java api haven't deterministic information on that.
   *
   * @return Machine ips ordered by relevance.
   */
  public static List<IP> findMachineIps() {
    return findInterfaces()
      .stream()
      .sorted(Comparator.comparingInt(NetworkInterface::getIndex))
      .flatMap(NetworkInterface::inetAddresses)
      .filter(it -> it.getAddress().length == IP.BYTES) // todo needs a filter to exclude virtual network cards
      .map(it -> IP.of(it.getHostAddress()))
      .sorted(Comparator.comparing(it -> {
        return it.raw().startsWith("127") ? Integer.MAX_VALUE : 0;
      }))
      .toList()
      ;
  }

  public static List<NetworkInterface> findInterfaces() {
    return network.findNetworkInterfaces()
      .filter(it -> {
        try {
          return it.isUp();
        } catch (SocketException e) {
          return false;
        }
      })
      .toList();
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
