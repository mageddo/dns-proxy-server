package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchingContainerService {

  private final DockerFacade dockerFacade;

  List<InspectContainerResponse> findMatchingContainers(HostnameQuery host) {
    return this.dockerFacade.findActiveContainers()
      .stream()
      .map(it -> this.dockerFacade.inspect(it.getId()))
      .filter(ContainerHostnameMatcher.buildPredicate(host))
      .toList();
  }
}
