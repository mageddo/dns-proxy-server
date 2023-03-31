package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static com.mageddo.dnsproxyserver.docker.ContainerSolvingService.NETWORK_BRIDGE;
import static com.mageddo.dnsproxyserver.docker.ContainerSolvingService.NETWORK_DPS;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DpsContainerManager {

  static final String DPS_INSIDE_CONTAINER = "1";

  private final ContainerSolvingService containerSolvingService;
  private final DockerDAO dockerDAO;
  private final DockerClient dockerClient;
  private final DockerNetworkDAO dockerNetworkDAO;

  public void setupNetwork() {
    final var configureNetwork = BooleanUtils.isTrue(Configs.getInstance().getDpsNetwork());
    log.info("status=dpsNetwork, active={}", configureNetwork);
    if (!configureNetwork) {
      return;
    }
    this.createWhereNotExists();
    this.connectDpsContainer();
  }

  void createWhereNotExists() {
    if (this.dockerNetworkDAO.existsByName(NETWORK_DPS)) {
      log.debug("status=dpsNetworkAlreadyExists");
      return;
    }
    final var currentVersion = Configs.getInstance().getVersion();
    final var res = this.dockerClient
      .createNetworkCmd()
      .withName(NETWORK_DPS)
      .withDriver(NETWORK_BRIDGE)
      .withCheckDuplicate(false)
      .withEnableIpv6(true)
      .withIpam(
        new Network.Ipam()
          .withConfig(
            new Network.Ipam.Config()
              .withSubnet("172.157.0.0/16")
              .withIpRange("172.157.5.3/24")
              .withGateway("172.157.5.1"),
            new Network.Ipam.Config()
              .withSubnet("fc00:5c6f:db50::/64")
              .withGateway("fc00:5c6f:db50::1")
          )
      )
      .withInternal(false)
      .withAttachable(true)
      .withLabels(Map.of(
        "description", "Dns Proxy Server Network: https://github.com/mageddo/dns-proxy-server",
        "version", currentVersion
      ))
      .exec();
    log.info("status=networkCreated, id={}, warnings={}", res.getId(), Arrays.toString(res.getWarnings()));
  }

  void connectDpsContainer() {
    final var container = this.findDpsContainer();
    if (container == null) {
      log.info("status=dps-container-not-found");
      return;
    }
    final var dpsContainerIP = "172.157.5.249";
    this.disconnectAnotherContainerWithSameIPFromDpsNetowrk(container.getId(), dpsContainerIP);
    this.connectDpsContainerToDpsNetwork(container, dpsContainerIP);
  }

  public Container findDpsContainer() {

    final var containers = this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Collections.singletonList("running"))
      .withLabelFilter(Collections.singletonList("dps.container=true"))
      .exec();

    if (containers.size() > 1) {
      log.warn("status=multiple-dps-containers-found, action=using-the-first, containers={}", Containers.toNames(containers));
    } else {
      log.debug("dpsContainersFound={}", containers.size());
    }
    return containers
      .stream()
      .findFirst()
      .orElse(null);
  }

  public IP findDpsContainerIP() {
    final var container = this.findDpsContainer();
    if (container == null) {
      return null;
    }
    final var containerInsp = this.dockerDAO.inspect(container.getId());
    final var ip = this.containerSolvingService.findBestIpMatch(containerInsp);
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return IP.of(ip);
  }

  public boolean isDpsRunningInsideContainer() {
    return StringUtils.equals(getDpsContainerEnv(), DPS_INSIDE_CONTAINER);
  }

  public static boolean isDpsContainer(Container c) {
    final var lbl = c.getLabels().get("dps.container");
    return Objects.equals(lbl, "true");
  }

  public static boolean isNotDpsContainer(Container container) {
    return !isDpsContainer(container);
  }

  void disconnectAnotherContainerWithSameIPFromDpsNetowrk(String containerId, String ip) {
    final var container = this.dockerNetworkDAO.findContainerWithIp(NETWORK_DPS, ip);
    if (container != null && !Objects.equals(containerId, container.getKey())) {
      log.info(
        "status=detachingContainerUsingDPSIpFromDpsNetwork, ip={}, oldContainerId={}, newContainerId={}",
        ip, containerId, container.getKey()
      );
      this.dockerNetworkDAO.disconnect(NETWORK_DPS, container.getKey());
    }
  }

  void connectDpsContainerToDpsNetwork(Container container, String ip) {
    final var foundIp = Networks.findIpv4Address(NETWORK_DPS, container);
    if (foundIp == null) {
      this.dockerNetworkDAO.connect(NETWORK_DPS, container.getId());
      log.info("status=dpsContainerConnectedToDpsNetwork, containerId={}, ip={}", container.getId(), ip);
    } else if (!Objects.equals(foundIp, ip)) {
      this.dockerNetworkDAO.disconnect(NETWORK_DPS, container.getId());
      this.dockerNetworkDAO.connect(NETWORK_DPS, container.getId(), ip);
      log.info(
        "status=dpsWasConnectedWithWrongIp, action=fixing, foundIp={}, rightIp={}, container={}",
        foundIp, ip, container.getId()
      );
    } else {
      log.debug("status=dpsContainerAlreadyConnectedToDpsNetwork, container={}", container.getId());
    }
  }

  String getDpsContainerEnv() {
    return System.getenv("DPS_CONTAINER");
  }

}
