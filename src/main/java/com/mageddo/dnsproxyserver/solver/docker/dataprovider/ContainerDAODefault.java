package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.dnsproxyserver.docker.application.ContainerPredicates;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.ContainerCompactMapper;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper.ContainerMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ContainerDAODefault implements ContainerDAO {

  private final com.mageddo.dnsproxyserver.docker.ContainerDAO containerDAO;
  private final DockerClient dockerClient;

  @Override
  public List<Container> findActiveContainersMatching(HostnameQuery query) {
    final var containers = this.findActiveContainers();
    return this.containerDAO.inspectFilteringValidContainers(containers)
        .filter(ContainerHostnameMatcher.buildPredicate(query))
        .map(ContainerMapper::of)
        .toList();
  }

  @Override
  public List<ContainerCompact> findNetworkContainers(String networkId) {
    return this.dockerClient.listContainersCmd()
        .withNetworkFilter(List.of(networkId))
        .exec()
        .stream()
        .filter(ContainerPredicates::isEnabledForDPS)
        .map(ContainerCompactMapper::of)
        .toList();
  }

  List<com.github.dockerjava.api.model.Container> findActiveContainers() {
    return this.containerDAO.findActiveContainers()
        .stream()
        .filter(ContainerPredicates::isEnabledForDPS)
        .toList()
        ;
  }

  @Override
  public boolean isEnabledForDPS(String containerId) {
    return ContainerPredicates.isEnabledForDPS(this.containerDAO.findById(containerId));
  }

}
