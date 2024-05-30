package com.mageddo.dnsproxyserver.solver.docker.application;

import com.mageddo.dnsproxyserver.solver.docker.dataprovider.ContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerNetworkService {

  private final NetworkDAO networkDAO;
  private final ContainerDAO containerDAO;
  private final DpsContainerDAO dpsContainerDAO;

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

  public void connectContainerTo(String networkName, String containerId) {
    if (this.dpsContainerDAO.isDpsContainer(containerId)) {
      log.info("status=won't connect dps container using conventional mode, containerId={}", containerId);
      return;
    }
    final var status = this.networkDAO.connect(networkName, containerId);
    log.info("status={}, networkName={}, containerId={}", status, networkName, containerId);
  }
}
