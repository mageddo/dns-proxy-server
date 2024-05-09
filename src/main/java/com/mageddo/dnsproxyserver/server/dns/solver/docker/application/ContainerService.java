package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DpsContainerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerService {

  private final DockerNetworkDAO dockerNetworkDAO;

  public void connectRunningContainers(){
    this.dockerNetworkDAO.connectRunningContainers(
      Network.Name.DPS.lowerCaseName(), DpsContainerUtils::isNotDpsContainer
    );
  }
}
