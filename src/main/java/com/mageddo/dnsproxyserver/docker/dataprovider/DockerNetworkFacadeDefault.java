package com.mageddo.dnsproxyserver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Network;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus;
import com.mageddo.net.Networks;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus.ALREADY_CONNECTED;
import static com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus.CONNECTED;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerNetworkFacadeDefault implements DockerNetworkFacade {

  private final DockerClient dockerClient;

  @Override
  public Network findById(String id) {
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
    final var network = this.dockerClient.listNetworksCmd()
      .withNameFilter("^" + networkName + "$")
      .exec()
      .stream()
      .findFirst()
      .orElse(null);
    log.debug("queryName={}, foundName={}", networkName, Objects.mapOrNull(network, Network::getName));
    return network;
  }

  @Override
  public Pair<String, ContainerNetwork> findContainerWithIp(String networkName, String ip) {
    final var network = this.findByName(networkName);
    Validate.notNull(network, "network not found: %s", networkName);
    final var containers = this.findNetworkContainers(network.getId());
    for (final var container : containers) {
      final var containerId = container.getId();
      if (StringUtils.contains(Networks.findIpv4Address(networkName, container), ip)) {
        return Pair.of(containerId, Networks.findContainerNetwork(networkName, container));
      }
    }
    return null;
  }

  @Override
  public List<Container> findNetworkContainers(String networkId) {
    return this.dockerClient.listContainersCmd()
      .withNetworkFilter(List.of(networkId))
      .exec();
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
  public NetworkConnectionStatus connect(String networkNameOrId, String containerId) {
    try {
      this.dockerClient
        .connectToNetworkCmd()
        .withNetworkId(networkNameOrId)
        .withContainerId(containerId)
        .exec()
      ;
      log.debug("status=connected, networkNameOrId={}, containerId={}", networkNameOrId, containerId);
      return CONNECTED;
    } catch (DockerException e) {
      if (e.getMessage().contains("already exists in network")) {
        return ALREADY_CONNECTED;
      }
      throw e;
    }
  }

  @Override
  public void connect(String networkNameOrId, String containerId, String ip) {

    final var builder = this.dockerClient.connectToNetworkCmd()
      .withNetworkId(networkNameOrId)
      .withContainerId(containerId);

    if (StringUtils.isNotBlank(ip)) {
      builder
        .withContainerNetwork(
          new ContainerNetwork()
            .withIpamConfig(new ContainerNetwork
              .Ipam()
              .withIpv4Address(ip)
            )
            .withIpv4Address(ip)
        );
    }
    builder.exec();
    log.info("status=network-connected, network={}, container={}, ip={}", networkNameOrId, containerId, ip);

  }

  @Override
  public boolean exists(String networkId) {
    return this.findById(networkId) != null;
  }


}
