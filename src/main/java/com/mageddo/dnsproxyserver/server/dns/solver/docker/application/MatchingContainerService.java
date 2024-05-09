package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerHostnameMatcher;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.ContainerMapper;
import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchingContainerService {

  // todo #444 must inject dao instead to decouple
  private final DockerFacade dockerFacade;

  List<Container> findMatchingContainers(HostnameQuery host) {
    return this.dockerFacade.findActiveContainers()
      .stream()
      .map(it -> this.dockerFacade.inspect(it.getId()))
      .filter(ContainerHostnameMatcher.buildPredicate(host))
      .map(ContainerMapper::of)
      .toList();
  }
}
