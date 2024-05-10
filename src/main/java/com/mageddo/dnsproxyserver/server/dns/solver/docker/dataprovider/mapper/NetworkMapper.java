package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.net.IP;

import java.util.List;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.application.DockerNetworkService.findGatewayIp;

public class NetworkMapper {
  public static Network of(com.github.dockerjava.api.model.Network n){
    return Network.builder()
      .name(n.getName())
      .driver(n.getDriver())
      .gateways(List.of(
        findGatewayIp(n, IP.Version.IPV4),
        findGatewayIp(n, IP.Version.IPV6)
      ))
      .ipv6Active(n.getEnableIPv6())
      .build()
      ;
  }
}
