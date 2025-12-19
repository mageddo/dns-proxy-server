package com.mageddo.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.mageddo.dnsproxyserver.utils.Ips;

import org.apache.commons.lang3.StringUtils;

import lombok.SneakyThrows;

public class Networks {

  volatile static Network network = Network.getInstance();

  public static IP findCurrentMachineIP() {
    return findCurrentMachineIP(IP.Version.IPV4);
  }

  @SneakyThrows
  public static IP findCurrentMachineIP(IP.Version version) {
    return findMachineIps()
        .stream()
        .filter(
            it -> it.version() == version) // todo needs a filter to exclude virtual network cards
        .min(Comparator.comparing(it -> {
          return it.isLoopback() ? Integer.MAX_VALUE : 0;
        }))
        .orElse(null);
  }

  /**
   * The "relevance" is understood as the IP which have most chances of represent the real
   * hardware network interface,
   * we say "most chances" because java api haven't deterministic information on that.
   *
   * @return Machine ips ordered by relevance.
   */
  public static List<IP> findMachineIps() {
    return findInterfaces()
        .stream()
        .sorted(Comparator.comparingInt(NetworkInterface::getIndex))
        .flatMap(NetworkInterface::inetAddresses)
        .map(it -> IP.of(it.getAddress()))
        .toList()
        ;
  }

  static List<NetworkInterface> findInterfaces() {
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

  // todo methods below are docker related methods so supposed to be on com.mageddo
  // .dnsproxyserver.docker.net package
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
        .map(StringUtils::trimToNull)
        .orElse(null);
  }

  public static String findIpv6Address(ContainerNetwork containerNetwork) {
    return Optional
        .ofNullable(containerNetwork)
        .map(ContainerNetwork::getGlobalIPv6Address)
        .map(StringUtils::trimToNull)
        .orElse(null);
  }

  public static String findIP(ContainerNetwork network, IP.Version version) {
    return switch (version) {
      case IPV4 -> findIpv4Address(network);
      case IPV6 -> findIpv6Address(network);
    };
  }

  public static boolean ping(String ip, int port, int timeout) {
    try {
      final InetAddress addr = InetAddress.getByAddress(Ips.toBytes(ip));
      return ping(addr, port, timeout);
    } catch (UnknownHostException e) {
      return false;
    }
  }

  public static boolean ping(InetSocketAddress address, int timeout) {
    try (var socket = new Socket()) {
      socket.connect(address, timeout);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean ping(InetAddress address, int port, int timeout) {
    return ping(new InetSocketAddress(address, port), timeout);
  }

}
