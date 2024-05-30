package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.net.IP;

import java.util.Objects;
import java.util.stream.Stream;

public class NetworkMapper {

  public static Network of(com.github.dockerjava.api.model.Network n) {
    return Network.builder()
      .name(n.getName())
      .driver(n.getDriver())
      .gateways(Stream
        .of(
          findGatewayIp(n, IP.Version.IPV4),
          findGatewayIp(n, IP.Version.IPV6)
        )
        .filter(Objects::nonNull)
        .toList()
      )
      .ipv6Active(n.getEnableIPv6())
      .build()
      ;
  }

  static IP findGatewayIp(com.github.dockerjava.api.model.Network network) {
    return findGatewayIp(network, IP.Version.IPV4);
  }

  static IP findGatewayIp(com.github.dockerjava.api.model.Network network, IP.Version version) {
    if (network == null) {
      return null;
    }
    return network
      .getIpam()
      .getConfig()
      .stream()
      .map(com.github.dockerjava.api.model.Network.Ipam.Config::getGateway)
      .map(IP::of)
      .filter(it -> it.version() == version)
      .findFirst()
      .orElse(null)
      ;
  }
}
