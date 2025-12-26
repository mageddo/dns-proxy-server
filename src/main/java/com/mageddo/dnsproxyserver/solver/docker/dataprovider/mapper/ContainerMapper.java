package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.mageddo.dnsproxyserver.config.Config.SolverDocker;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.docker.application.Labels;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.Label;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;

import org.apache.commons.lang3.StringUtils;

public class ContainerMapper {

  public static Container of(InspectContainerResponse inspect) {
    return of(inspect, findPreferred());
  }

  public static Container of(
      InspectContainerResponse inspect, SolverDocker.Networks.Preferred preferred
  ) {
    final var foundNetworks = buildNetworks(inspect);
    final var possibleNetworksNames = buildNetworkNames(inspect, preferred);
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

  static Set<String> buildNetworkNames(
      InspectContainerResponse c, SolverDocker.Networks.Preferred preferred
  ) {
    if (preferred.isOverrideDefault() && preferred.getNames() != null) {
      return mapPrincipalNetworkWith(c, preferred.getNames());
    }
    if (preferred.getNames() == null) {
      return buildDefaultWithPrincipal(c);
    }
    return mapPrincipalNetworkWith(c, preferred.getNames(), buildDefault());
  }

  @SafeVarargs
  private static Set<String> mapPrincipalNetworkWith(
      InspectContainerResponse c, Collection<String>... namesCollections
  ) {
    final var set = new LinkedHashSet<String>();
    final var principal = mapPrincipalNetworkName(c);
    if (StringUtils.isNotBlank(principal)) {
      set.add(principal);
    }
    for (var names : namesCollections) {
      set.addAll(names);
    }
    return set;
  }

  private static LinkedHashSet<String> buildDefault() {
    return buildDefault(null);
  }

  private static LinkedHashSet<String> buildDefaultWithPrincipal(InspectContainerResponse c) {
    return buildDefault(mapPrincipalNetworkName(c));
  }

  private static String mapPrincipalNetworkName(InspectContainerResponse c) {
    return Labels.findValue(c, Label.DPS_DEFAULT_NETWORK);
  }

  private static LinkedHashSet<String> buildDefault(String principalNetworkName) {
    return Stream.of(
            principalNetworkName,
            Network.Name.DPS.lowerCaseName(),
            Network.Name.BRIDGE.lowerCaseName()
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static SolverDocker.Networks.Preferred findPreferred() {
    return Configs.getInstance()
        .getSolverDocker()
        .getNetworks()
        .getPreferred();
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
