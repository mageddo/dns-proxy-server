package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.mageddo.dnsproxyserver.docker.Containers;
import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper.ContainerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerDAODefault implements ContainerDAO {

  private final DockerClient dockerClient;
  private final DockerFacade dockerFacade;

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
      .map(it -> this.dockerFacade.inspect(it.getId()))
      .map(ContainerMapper::of)
      .orElse(null);
  }
}
