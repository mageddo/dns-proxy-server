package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.commons.lang.Objects;
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
  public IP findHostMachineIp() {
    return IP.of(this.findHostMachineIpRaw());
  }

  @Override
  public String findHostMachineIpRaw() {
    return DockerNetworkService.findGatewayIp(this.findBestNetwork());
  }

  @Override
  public boolean isConnected() {
    return this.connectionCheck.isConnected();
  }

  @Override
  public List<Container> findActiveContainers() {
    final var activeContainers = this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Containers.RUNNING_STATUS_LIST)
      .withLimit(1024)
//      .withNetworkFilter()
      .exec();
    return activeContainers;
  }

  @Override
  public InspectContainerResponse inspect(String id) {
    return this.dockerClient.inspectContainerCmd(id).exec();
  }

  Network findBestNetwork() {
    final var network = this.dockerClient.listNetworksCmd()
      .exec()
      .stream()
      .min(NetworkComparator::compare)
      .orElse(null);
    log.debug(
      "status=bestNetwork, network={}, ip={}",
      Objects.mapOrNull(network, Network::getName),
      DockerNetworkService.findGatewayIp(network)
    );
    return network;
  }


}
