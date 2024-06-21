package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.net.IP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class NetworkMapper {

  public static final int CHAR_NOT_FOUND = -1;
  public static final char SUBNET_MASK_SEPARATOR = '/';

  public static Network of(com.github.dockerjava.api.model.Network n) {
    log.debug("status=mapping, networkName={}", n.getName());
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
    final var ipam = network.getIpam();
    if (ipam != null && ipam.getConfig() != null) {
      return ipam
        .getConfig()
        .stream()
        .map(com.github.dockerjava.api.model.Network.Ipam.Config::getGateway)
        .map(NetworkMapper::extractIpIfNeedledWhenGatewayIsSubnet)
        .map(IP::of)
        .filter(it -> it.version() == version)
        .findFirst()
        .orElse(null)
        ;
    }
    return null;
  }

  private static String extractIpIfNeedledWhenGatewayIsSubnet(String str) {
    final int index = str.indexOf(SUBNET_MASK_SEPARATOR);
    if (index == CHAR_NOT_FOUND) {
      return str;
    }
    return StringUtils.substring(str, 0, index);
  }
}
