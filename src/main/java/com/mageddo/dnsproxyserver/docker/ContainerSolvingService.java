package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.net.Networks;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.dnsproxyserver.docker.Labels.DEFAULT_NETWORK_LABEL;
import static com.mageddo.dnsproxyserver.docker.domain.Network.BRIDGE;
import static com.mageddo.dnsproxyserver.docker.domain.Network.DPS;
import static com.mageddo.dnsproxyserver.docker.domain.Network.HOST;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerSolvingService {

  public static final String NETWORK_DPS = DPS.lowerCaseName();
  public static final String NETWORK_BRIDGE = BRIDGE.lowerCaseName();
  public static final String NETWORK_MODE_HOST = HOST.lowerCaseName();

  private final DockerDAO dockerDAO;
  private final DockerNetworkDAO networkDAO;

  public String findBestHostIP(Hostname host) {
    final var stopWatch = StopWatch.createStarted();
    final var matchedContainers = this.findMatchingContainers(host);
    final var foundIp = matchedContainers
      .stream()
      .map(this::findBestIpMatch)
      .findFirst()
      .orElse(null);
    log.trace("status=findDone, host={}, found={}, time={}", host, foundIp, stopWatch.getTime());
    return foundIp;
  }

  public String findBestIpMatch(InspectContainerResponse inspect) {
    return this.findBestIpMatch(inspect, buildNetworks(inspect), this.dockerDAO::findHostMachineIpRaw);
  }


  public String findBestIpMatch(
    InspectContainerResponse c, Collection<String> networksNames, Supplier<String> hostMachineSup
  ) {

    final var networks = c
      .getNetworkSettings()
      .getNetworks();

    for (final var name : networksNames) {
      if (!networks.containsKey(name)) {
        log.debug("status=networkNotFoundForContainer, name={}", name);
        continue;
      }
      final var ip = networks.get(name).getIpAddress();
      log.debug("status=foundIp, network={}, container={}, ip={}", name, c.getName(), ip);
      return ip;
    }
    log.debug(
      "status=predefinedNetworkNotFound, action=findSecondOption, searchedNetworks={}, container={}",
      networksNames, c.getName()
    );

    return networks
      .keySet()
      .stream()
      .map(nId -> {
        final var network = this.networkDAO.findByName(nId);
        if (network == null) {
          log.warn("status=networkIsNull, id={}", nId);
        }
        return network;
      })
      .filter(Objects::nonNull)
      .min(NetworkComparator::compare)
      .map(network -> {
        final var networkName = network.getName();
        final var ip = Networks.findIpv4Address(networks.get(networkName));
        log.debug(
          "status=foundIp, networks={}, networkName={}, driver={}, foundIp={}",
          networks.keySet(), networkName, network.getDriver(), ip
        );
        return StringUtils.trimToNull(ip);
      })
      .filter(StringUtils::isNotBlank)
      .orElseGet(() -> {
        return Optional
          .ofNullable(buildDefaultIp(c))
          .orElseGet(() -> {
            final var hostIp = hostMachineSup.get();
            log.debug("status=noNetworkAvailable, usingHostMachineIp={}", hostIp);
            return hostIp;
          })
          ;
      })
      ;

  }

  static String buildDefaultIp(InspectContainerResponse c) {
    return StringUtils.trimToNull(c
      .getNetworkSettings()
      .getIpAddress()
    );
  }


  static Set<String> buildNetworks(InspectContainerResponse c) {
    return Stream.of(
        Labels.findLabelValue(c.getConfig(), DEFAULT_NETWORK_LABEL),
        NETWORK_DPS,
        NETWORK_BRIDGE
      )
      .filter(Objects::nonNull)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private List<InspectContainerResponse> findMatchingContainers(Hostname host) {
    return this.dockerDAO.findActiveContainers()
      .stream()
      .map(it -> this.dockerDAO.inspect(it.getId()))
      .filter(ContainerHostnameMatcher.buildPredicate(host))
      .toList();
  }

}
