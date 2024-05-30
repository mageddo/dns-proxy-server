package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.docker.application.DockerConnectionCheck;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.application.NetworkComparator;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.NetworkMapper;
import com.mageddo.net.IP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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
    final var network = this.findNetworks()
      .stream()
      .filter(it -> java.util.Objects.equals(it.isIpv6Active(), version.isIpv6()))
      .min(NetworkComparator::compare)
      .orElse(null);
    log.debug("status=bestNetwork, network={}", network);
    return network;
  }

  List<Network> findNetworks() {
    return this.dockerClient.listNetworksCmd()
      .exec()
      .stream()
      .map(NetworkMapper::of)
      .toList();
  }
}
