package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.docker.DockerConnectionCheck;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.NetworkComparator;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.NetworkMapper;
import com.mageddo.net.IP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAODefault implements DockerDAO {

  private final DockerClient dockerClient;
  private final DockerConnectionCheck connectionCheck;

  @Override
  public boolean isConnected() {
    return this.connectionCheck.isConnected();
  }


  @Override
  public IP findHostMachineIp(IP.Version version) {
    return Objects.mapOrNull(this.findBestNetwork(version), (network) -> network.getGateway(version));
  }

  Network findBestNetwork(IP.Version version) {
    final var network = this.dockerClient.listNetworksCmd()
      .exec()
      .stream()
      .filter(it -> java.util.Objects.equals(it.getEnableIPv6(), version.isIpv6()))
      .map(NetworkMapper::of)
      .min(NetworkComparator::compare)
      .orElse(null);
    log.debug("status=bestNetwork, network={}", network);
    return network;
  }
}
