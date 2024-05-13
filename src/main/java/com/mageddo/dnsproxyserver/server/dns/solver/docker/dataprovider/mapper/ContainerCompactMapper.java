package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DpsContainerUtils;

import java.util.Arrays;

public class ContainerCompactMapper {

  public static ContainerCompact of(Container c) {
    return ContainerCompact
      .builder()
      .id(c.getId())
      .name(Arrays
        .stream(c.getNames())
        .findFirst()
        .orElse("Unknown container")
      )
      .dpsContainer(DpsContainerUtils.isDpsContainer(c))
      .build();
  }
}
