package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerDAO;
import lombok.RequiredArgsConstructor;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchingContainerService {

  private final ContainerDAO containerDAO;

  List<Container> findMatchingContainers(HostnameQuery host) {
    return this.containerDAO.findActiveContainersInspectMatching(host);
  }
}
