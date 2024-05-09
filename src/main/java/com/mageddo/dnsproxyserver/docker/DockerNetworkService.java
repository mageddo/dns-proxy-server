package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.net.IP;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


import static com.mageddo.dnsproxyserver.server.dns.solver.docker.Network.Name;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerNetworkService {

  private final DockerNetworkFacade networkDAO;
  private final ContainerDAO containerDAO;

  public static IP findGatewayIp(Network network) {
    return findGatewayIp(network, IP.Version.IPV4);
  }

  public static IP findGatewayIp(Network network, IP.Version version) {
    if (network == null) {
      return null;
    }
    return network
      .getIpam()
      .getConfig()
      .stream()
      .map(Network.Ipam.Config::getGateway)
      .map(IP::of)
      .filter(it -> it.version() == version)
      .findFirst()
      .orElse(null)
      ;
  }

  public static Boolean isHostNetwork(Container container) {
    final var config = container.getHostConfig();
    if (config == null) {
      return null;
    }
    final var networkMode = config.getNetworkMode();
    return Name.HOST.equalTo(networkMode);
  }

  public List<String> disconnectContainers(String id) {
    final var removedContainers = new ArrayList<String>();
    final var network = this.networkDAO.findById(id);
    if (network == null) {
      return null;
    }
    final var containers = this.containerDAO.findNetworkContainers(id);
    for (final var container : containers) {
      this.networkDAO.disconnect(id, container.getId());
      removedContainers.add(container.getId());
    }
    return removedContainers;
  }

  public void connect(String networkName, String containerId) {
    if (this.containerDAO.isDpsContainer(containerId)) {
      log.info("status=won't connect dps container using conventional mode, containerId={}", containerId);
      return;
    }
    final var status = this.networkDAO.connect(networkName, containerId);
    log.info("status={}, networkName={}, containerId={}", status, networkName, containerId);
  }
}
