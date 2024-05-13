package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.dnsproxyserver.docker.dataprovider.ContainerFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.ContainerCompactMapper;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.ContainerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Singleton
@Default
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerDAODefault implements ContainerDAO {

  private final ContainerFacade containerFacade;
  private final DockerClient dockerClient;

  @Override
  public List<Container> findActiveContainersMatching(HostnameQuery query) {
    return this.containerFacade.findActiveContainers()
      .stream()
      .map(it -> this.containerFacade.inspect(it.getId()))
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
      .map(ContainerCompactMapper::of)
      .toList();
  }

}
