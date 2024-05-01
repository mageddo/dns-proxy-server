package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
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

  private final DockerDAO dockerDAO;

  List<InspectContainerResponse> findMatchingContainers(HostnameQuery host) {
    return this.dockerDAO.findActiveContainers()
      .stream()
      .map(it -> this.dockerDAO.inspect(it.getId()))
      .filter(ContainerHostnameMatcher.buildPredicate(host))
      .toList();
  }
}
