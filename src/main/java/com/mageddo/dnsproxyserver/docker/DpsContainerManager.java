package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.config.Configs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DpsContainerManager {

  private final DockerClient dockerClient;
  private final DockerNetworkDAO dockerNetworkDAO;

  public void setupNetwork() {
    final var configureNetwork = BooleanUtils.isTrue(Configs.getInstance().getDpsNetwork());
    log.info("status=dpsNetwork, active={}", configureNetwork);
    if (!configureNetwork) {
      return;
    }
    create();
    connectDpsContainer();
  }

  void create() {
    final var currentVersion = Configs.getInstance().getVersion();
    this.dockerClient.createNetworkCmd()
      .withDriver(DockerNetworks.NETWORK_BRIDGE)
      .withCheckDuplicate(false)
      .withEnableIpv6(false)
      .withIpam(
        new com.github.dockerjava.api.model.Network.Ipam()
          .withConfig(
            new Network.Ipam.Config()
              .withSubnet("172.157.0.0/16")
              .withIpRange("172.157.5.3/24")
              .withGateway("172.157.5.1")
          )
      )
      .withInternal(false)
      .withAttachable(true)
      .withLabels(Map.of(
        "description", "Dns Proxy Server Network: https://github.com/mageddo/dns-proxy-server",
        "version", currentVersion
      ))
    ;
  }

  void connectDpsContainer() {
    final var container = this.findDpsContainer();
    if (container == null) {
      log.info("status=dps-container-not-found");
      return;
    }
    final var dpsContainerIP = "172.157.5.249";
    disconnectAnotherContainerWithSameIPFromDpsNetowrk(container.getId(), dpsContainerIP);
  }

  Container findDpsContainer() {
    final var containers = this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Collections.singletonList("running"))
      .withLabelFilter(Collections.singletonList("dps.container=true"))
      .exec();

    if (containers.size() > 1) {
      log.warn(
        "status=multiple-dps-containers-found, action=using-the-first, containers={}", Containers.toNames(containers)
      );
    }
    return containers
      .stream()
      .findFirst()
      .orElse(null)
      ;
  }

  void disconnectAnotherContainerWithSameIPFromDpsNetowrk(String containerId, String ip) {
    final var dpsNetwork = DockerNetworks.NETWORK_DPS;
    final var container = this.dockerNetworkDAO.findContainerWithIp(dpsNetwork, ip);
    if (container == null) {
      this.dockerNetworkDAO.connect(dpsNetwork, containerId);
    } else if (!Objects.equals(containerId, container.getKey())) {
      log.info(
        "status=detachingContainerUsingDPSIpFromDpsNetwork, ip={}, oldContainerId={}, newContainerId={}",
        ip, containerId, container.getKey()
      );
      this.dockerNetworkDAO.disconnect(dpsNetwork, container.getKey());
    } else {
      log.info(
        "status=dpsAlreadyConnectedToDpsNetwork, containerId={}, ip={}, network={}",
        containerId, ip, dpsNetwork
      );
    }
  }


}
