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
    return this.dockerClient.listNetworksCmd().withIdFilter(id)
      .exec()
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }

  @Override
  public Pair<String, Network.ContainerNetworkConfig> findContainerWithIp(String networkId, String ip) {
    final var network = this.findNetwork(networkId);
    Validate.notNull(network, "network not found: %s", networkId);
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
  public void connect(String networkId, String containerId) {
    this.dockerClient
      .connectToNetworkCmd()
      .withNetworkId(networkId)
      .withContainerId(containerId)
      .exec()
    ;
    log.info("status=connected, networkId={}, containerId={}", networkId, containerId);
  }

  @Override
  public void connet(String networkId, String containerId, String ip) {

    final var builder = this.dockerClient.connectToNetworkCmd()
      .withNetworkId(networkId)
      .withContainerId(containerId);

    if (StringUtils.isNotBlank(ip)) {
      final var config = builder.getContainerConfig();
      if (config != null) {
        config.withIpv4Address(ip);
      } else {
        log.warn("status=couldntSetIp, networkId={}, ip={}", networkId, ip);
      }
    }
    builder.exec();
    log.info("status=network-connected, network={}, container={}", networkId, containerId);

  }
}
