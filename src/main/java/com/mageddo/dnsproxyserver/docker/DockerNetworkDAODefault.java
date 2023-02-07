package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerNetworkDAODefault implements DockerNetworkDAO {

  private final DockerClient dockerClient;

  @Override
  public Network findNetwork(String id) {
    return this.dockerClient.listNetworksCmd()
      .withIdFilter("^" + id)
      .exec()
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }

  @Override
  public Network findByName(String networkName) {
    return this.dockerClient.listNetworksCmd()
      .withNameFilter(networkName)
      .exec()
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }

  @Override
  public Pair<String, Network.ContainerNetworkConfig> findContainerWithIp(String networName, String ip) {
    final var network = this.findByName(networName);
    Validate.notNull(network, "network not found: %s", networName);
    final var containers = network.getContainers();
    for (final var containerId : containers.keySet()) {
      final var container = containers.get(containerId);
      if (container.getIpv4Address().contains(ip)) {
        return Pair.of(containerId, container);
      }
    }
    return null;
  }

  @Override
  public void disconnect(String networkId, String containerId) {
    this.dockerClient
      .disconnectFromNetworkCmd()
      .withNetworkId(networkId)
      .withContainerId(containerId)
      .exec()
    ;
    log.info("status=disconnected, networkId={}, containerId={}", networkId, containerId);
  }

  @Override
  public void connect(String networkNameOrId, String containerId) {
    this.dockerClient
      .connectToNetworkCmd()
      .withNetworkId(networkNameOrId)
      .withContainerId(containerId)
      .exec()
    ;
    log.info("status=connected, networkNameOrId={}, containerId={}", networkNameOrId, containerId);
  }

  @Override
  public void connect(String networkNameOrId, String containerId, String ip) {

    final var builder = this.dockerClient.connectToNetworkCmd()
      .withNetworkId(networkNameOrId)
      .withContainerId(containerId);

    if (StringUtils.isNotBlank(ip)) {
      final var config = builder.getContainerConfig();
      if (config != null) {
        config.withIpv4Address(ip);
      } else {
        log.warn("status=couldntSetIp, networkNameOrId={}, ip={}", networkNameOrId, ip);
      }
    }
    builder.exec();
    log.info("status=network-connected, network={}, container={}", networkNameOrId, containerId);

  }

  @Override
  public void connectRunningContainers(String networkName) {
    this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Containers.RUNNING_STATUS_LIST)
      .exec()
      .stream()
      .filter(it -> Boolean.FALSE.equals(DockerNetworks.isHostNetwork(it)))
      .filter(it -> !Containers.containsNetworkName(it, networkName))
      .forEach(container -> this.connect(networkName, container.getId()))
    ;
  }

  @Override
  public boolean exists(String networkId) {
    return this.findNetwork(networkId) != null;
  }

  @Override
  public boolean existsByName(String networkName) {
    return this.findByName(networkName) != null;
  }
}
