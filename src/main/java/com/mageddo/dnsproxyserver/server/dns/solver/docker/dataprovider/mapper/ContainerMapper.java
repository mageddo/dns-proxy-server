package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerSolvingAdapter;

public class ContainerMapper {
  public static Container of(InspectContainerResponse inspect) {
    return Container
      .builder()
      .id(inspect.getId())
      .networkNames(ContainerSolvingAdapter.buildNetworks(inspect))
      .build();
  }
}
