package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network.Ipam;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.docker.application.Containers;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacade;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.ContainerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DpsContainerDAODefault implements DpsContainerDAO {

  static final String DPS_INSIDE_CONTAINER_YES = "1";

  private final DockerClient dockerClient;
  private final ContainerFacade containerFacade;

  @Override
  public boolean isDpsRunningInsideContainer() {
    return StringUtils.equals(this.getDpsContainerEnvValue(), DPS_INSIDE_CONTAINER_YES);
  }

  @Override
  public boolean isDpsContainer(String containerId) {
    return DpsContainerUtils.isDpsContainer(this.containerFacade.findById(containerId));
  }

  @Override
  public Container findDPSContainer() {

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
      .map(it -> this.containerFacade.inspect(it.getId()))
      .map(ContainerMapper::of)
      .orElse(null);
  }

  @Override
  public void createDpsNetwork() {
    final var currentVersion = Configs.getInstance().getVersion();
    final var res = this.dockerClient
      .createNetworkCmd()
      .withName(Network.Name.DPS.lowerCaseName())
      .withDriver(Network.Name.BRIDGE.lowerCaseName())
      .withCheckDuplicate(false)
      .withEnableIpv6(true)
      .withIpam(buildIpAddressManagement())
      .withInternal(false)
      .withAttachable(true)
      .withLabels(Map.of(
        "description", "Dns Proxy Server Name: https://github.com/mageddo/dns-proxy-server",
        "version", currentVersion
      ))
      .exec();
    log.info("status=networkCreated, id={}, warnings={}", res.getId(), Arrays.toString(res.getWarnings()));
  }

  static Ipam buildIpAddressManagement() {
    return new Ipam()
      .withConfig(
        new Ipam.Config()
          .withSubnet("172.157.0.0/16")
          .withIpRange("172.157.5.3/24")
          .withGateway("172.157.5.1"),
        new Ipam.Config()
          .withSubnet("fc00:5c6f:db50::/64")
          .withGateway("fc00:5c6f:db50::1")
      );
  }

  String getDpsContainerEnvValue() {
    return System.getenv("DPS_CONTAINER");
  }
}
