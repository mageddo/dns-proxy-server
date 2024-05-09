package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.docker.Labels;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContainerMapper {

  public static Container of(InspectContainerResponse inspect) {
    return Container
      .builder()
      .id(inspect.getId())
      .networkNames(buildNetworks(inspect))
      .build();
  }

  static Set<String> buildNetworks(InspectContainerResponse c) {
    return Stream.of(
        Labels.findLabelValue(c.getConfig(), Labels.DEFAULT_NETWORK_LABEL),
        Network.Name.DPS.lowerCaseName(),
        Network.Name.BRIDGE.lowerCaseName()
      )
      .filter(Objects::nonNull)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
