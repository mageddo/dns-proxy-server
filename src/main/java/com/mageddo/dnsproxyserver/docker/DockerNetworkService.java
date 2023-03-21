package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mageddo.dnsproxyserver.docker.ContainerSolvingService.NETWORK_MODE_HOST;
import static com.mageddo.dnsproxyserver.docker.DpsContainerManager.isDpsContainer;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerNetworkService {

  private final DockerNetworkDAO networkDAO;
  private final ContainerDAO containerDAO;

  public static String findIp(Network network) {
    if (network == null) {
      return null;
    }
    return network
      .getIpam()
      .getConfig()
      .get(0)
      .getGateway() // fixme also get ipv6.
      ;
  }

  public static Boolean isHostNetwork(Container container) {
    final var config = container.getHostConfig();
    if (config == null) {
      return null;
    }
    final var networkMode = config.getNetworkMode();
    return Objects.equals(networkMode, NETWORK_MODE_HOST);
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
    if (isDpsContainer(this.containerDAO.findById(containerId))) {
      log.info("status=won't connect dps container using conventional mode, containerId={}", containerId);
      return;
    }
    this.networkDAO.connect(networkName, containerId);
  }
}
