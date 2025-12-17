package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.mageddo.dnsproxyserver.docker.application.Labels;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;

import org.apache.commons.lang3.StringUtils;

public class ContainerMapper {

  public static final String DEFAULT_NETWORK_LABEL = "dps.network";

  public static Container of(InspectContainerResponse inspect) {
    final var foundNetworks = buildNetworks(inspect);
    final var possibleNetworksNames = buildNetworkNames(inspect);
    return Container
        .builder()
        .id(inspect.getId())
        .name(inspect.getName())
        .preferredNetworkNames(possibleNetworksNames)
        .networks(foundNetworks)
        .ips(Stream.of(
                    buildDefaultIp(inspect, IP.Version.IPV4),
                    buildDefaultIp(inspect, IP.Version.IPV6)
                )
                .filter(Objects::nonNull)
                .toList()
        )
        .build();
  }

  static Map<String, Container.Network> buildNetworks(InspectContainerResponse inspect) {
    final var networks = new LinkedHashMap<String, Container.Network>();
    inspect.getNetworkSettings()
        .getNetworks()
        .forEach((k, v) -> {
          networks.put(k, toNetwork(v));
        });
    return networks;
  }

  static Container.Network toNetwork(ContainerNetwork n) {
    return Container.Network
        .builder()
        .ips(Stream.of(
                    IP.of(Networks.findIpv4Address(n)),
                    IP.of(Networks.findIpv6Address(n)
                    )
                )
                .filter(Objects::nonNull)
                .toList()
        )
        .build()
        ;
  }

  static Set<String> buildNetworkNames(InspectContainerResponse c) {
    return Stream.of(
            Labels.findLabelValue(c.getConfig(), DEFAULT_NETWORK_LABEL),
            Network.Name.DPS.lowerCaseName(),
            Network.Name.BRIDGE.lowerCaseName()
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  static IP buildDefaultIp(InspectContainerResponse c, IP.Version version) {
    final var settings = c.getNetworkSettings();
    if (settings == null) {
      return null;
    }
    if (version.isIpv6()) {
      return IP.of(StringUtils.trimToNull(settings.getGlobalIPv6Address()));
    }
    return IP.of(StringUtils.trimToNull(settings.getIpAddress()));
  }
}
