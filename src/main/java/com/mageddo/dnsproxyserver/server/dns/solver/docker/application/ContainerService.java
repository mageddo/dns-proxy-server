package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.docker.DpsContainerManager;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;

public class ContainerService {

  private final DockerNetworkDAO dockerNetworkDAO;

  public void connectRunningContainers(){
    this.dockerNetworkDAO.connectRunningContainers(
      Network.Name.DPS.lowerCaseName(), DpsContainerManager::isNotDpsContainer
    );
  }
}
