package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.docker.application.Containers;
import com.mageddo.dnsproxyserver.docker.ContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.Label;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.ContainerMapper;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.IpamMapper;

import org.apache.commons.lang3.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DpsContainerDAODefault implements DpsContainerDAO {

  static final String DPS_INSIDE_CONTAINER_YES = "1";

  private final DockerClient dockerClient;
  private final ContainerDAO containerDAO;

  @Override
  public boolean isDpsRunningInsideContainer() {
    return StringUtils.equals(this.getDpsContainerEnvValue(), DPS_INSIDE_CONTAINER_YES);
  }

  @Override
  public boolean isDpsContainer(String containerId) {
    return DpsContainerUtils.isDpsContainer(this.containerDAO.findById(containerId));
  }

  @Override
  public Container findDPSContainer() {

    final var containers = this.dockerClient
        .listContainersCmd()
        .withStatusFilter(Collections.singletonList("running"))
            .withLabelFilter(Collections.singletonList(Label.DPS_CONTAINER + "=true"))
        .exec();

    if (containers.size() > 1) {
      log.warn("status=multiple-dps-containers-found, action=using-the-first, containers={}",
          Containers.toNames(containers)
      );
    } else {
      log.debug("dpsContainersFound={}", containers.size());
    }
    return this.containerDAO.inspectFilteringValidContainers(containers)
        .findFirst()
        .map(ContainerMapper::of)
        .orElse(null);
  }

  @Override
  public void createDpsNetwork() {
    final var config = Configs.getInstance();
    final var currentVersion = config.getVersion();
    final var network = config.getSolverDocker()
        .getDpsNetwork();
    final var ipamConfigs = IpamMapper.of(network.getConfigs());
    final var res = this.dockerClient
        .createNetworkCmd()
        .withName(Network.Name.DPS.lowerCaseName())
        .withDriver(Network.Name.BRIDGE.lowerCaseName())
        .withCheckDuplicate(false)
        .withEnableIpv6(true)
        .withIpam(ipamConfigs)
        .withInternal(false)
        .withAttachable(true)
        .withLabels(Map.of(
            "description", "Dns Proxy Server Name: https://github.com/mageddo/dns-proxy-server",
            "version", currentVersion
        ))
        .exec();
    log.info(
        "status=networkCreated, id={}, warnings={}",
        res.getId(), Arrays.toString(res.getWarnings())
    );
  }

  String getDpsContainerEnvValue() {
    return System.getenv("DPS_CONTAINER");
  }
}
