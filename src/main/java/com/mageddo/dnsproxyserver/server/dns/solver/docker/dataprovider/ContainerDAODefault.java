package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacade;
import com.mageddo.dnsproxyserver.docker.application.Containers;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.ContainerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Slf4j
@Singleton
@Default
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerDAODefault implements ContainerDAO {

  private final DockerClient dockerClient;
  private final ContainerFacade containerFacade;

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
  public List<Container> findActiveContainersInspectMatching(HostnameQuery query) {
    return this.containerFacade.findActiveContainers()
      .stream()
      .map(it -> this.containerFacade.inspect(it.getId()))
      .filter(ContainerHostnameMatcher.buildPredicate(query))
      .map(ContainerMapper::of)
      .toList();
  }

  @Override
  public boolean isDpsContainer(String containerId) {
    return DpsContainerUtils.isDpsContainer(this.containerFacade.findById(containerId));
  }
}
