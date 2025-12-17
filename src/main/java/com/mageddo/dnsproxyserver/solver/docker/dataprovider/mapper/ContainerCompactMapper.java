package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import java.util.Arrays;

import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerUtils;

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
