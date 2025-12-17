package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import java.util.List;

import com.github.dockerjava.api.model.Network;
import com.mageddo.commons.Collections;
import com.mageddo.dnsproxyserver.config.Config.SolverDocker.DpsNetwork.NetworkConfig;

public class IpamMapper {

  public static Network.Ipam of(List<NetworkConfig> configs) {
    return new Network.Ipam()
        .withConfig(Collections.map(configs, IpamMapper::toConfig))
        ;
  }

  private static Network.Ipam.Config toConfig(NetworkConfig config) {
    return new Network.Ipam.Config()
        .withSubnet(config.getSubNet())
        .withIpRange(config.getIpRange())
        .withGateway(config.getGateway())
        ;
  }
}
