package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.commons.lang.Objects.mapOrNull;
import static com.mageddo.dnsproxyserver.docker.Labels.DEFAULT_NETWORK_LABEL;
import static com.mageddo.dnsproxyserver.docker.domain.Network.BRIDGE;
import static com.mageddo.dnsproxyserver.docker.domain.Network.DPS;
import static com.mageddo.dnsproxyserver.docker.domain.Network.HOST;

/**
 * Todo that's an application service with is high coupled to infrastructure docker adapter,
 *      the docker api classes  must be isolated to a port then that port be used on that service.
 *      See hexagonal architecture.
 */

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
  private final MatchingContainerService matchingContainerService;

  public Entry findBestMatch(HostnameQuery host) {
    final var stopWatch = StopWatch.createStarted();
    final var matchedContainers = this.matchingContainerService.findMatchingContainers(host);
    final var foundIp = matchedContainers
      .stream()
      .map(it -> this.findBestIpMatch(it, host.getVersion()))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
    final var hostnameMatched = !matchedContainers.isEmpty() && foundIp != null;
    log.trace(
      "status=findDone, host={}, found={}, hostnameMatched={}, time={}",
      host, foundIp, hostnameMatched, stopWatch.getTime()
    );
    return Entry
      .builder()
      .hostnameMatched(hostnameMatched)
      .ip(IP.of(foundIp))
      .build();
  }

  public String findBestIpMatch(InspectContainerResponse inspect) {
    return this.findBestIpMatch(inspect, IP.Version.IPV4);
  }

  public String findBestIpMatch(InspectContainerResponse inspect, IP.Version version) {
    return this.findBestIpMatch(
      inspect,
      buildNetworks(inspect),
      () -> mapOrNull(this.dockerDAO.findHostMachineIp(version), IP::toText), // todo there is no need to pass it as an argument, just build this supplier when it is needled
      version
    );
  }

  String findBestIpMatch(
    InspectContainerResponse c,
    Collection<String> networksNames,
    Supplier<String> hostMachineSup,
    IP.Version version
  ) {

    final var networks = c
      .getNetworkSettings()
      .getNetworks();

    for (final var name : networksNames) {
      if (!networks.containsKey(name)) {
        log.debug("status=networkNotFoundForContainer, name={}", name);
        continue;
      }
      final var containerNetwork = networks.get(name);
      final String ip = Networks.findIP(containerNetwork, version);
      log.debug("status=foundIp, network={}, container={}, ip={}", name, c.getName(), ip);
      if (StringUtils.isNotBlank(ip)) {
        return ip;
      }
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
        final var ip = Networks.findIP(networks.get(networkName), version);
        log.debug(
          "status=foundIp, networks={}, networkName={}, driver={}, foundIp={}",
          networks.keySet(), networkName, network.getDriver(), ip
        );
        return StringUtils.trimToNull(ip);
      })
      .filter(StringUtils::isNotBlank)
      .orElseGet(() -> {
        return Optional
          .ofNullable(buildDefaultIp(c, version))
          .orElseGet(() -> buildHostMachineIpWhenActive(hostMachineSup));
      })
      ;

  }

  String buildHostMachineIpWhenActive(Supplier<String> hostMachineSup) {
    if(isDockerSolverHostMachineFallbackActive()){
      final var hostIp = hostMachineSup.get();
      log.debug("status=noNetworkAvailable, usingHostMachineIp={}", hostIp);
      return hostIp;
    }
    log.debug("dockerSolverHostMachineFallback=inactive");
    return null;
  }

  boolean isDockerSolverHostMachineFallbackActive() {
    return Configs.getInstance().isDockerSolverHostMachineFallbackActive();
  }

  static String buildDefaultIp(InspectContainerResponse c, IP.Version version) {
    final var settings = c.getNetworkSettings();
    if (settings == null) {
      return null;
    }
    if (version.isIpv6()) {
      return StringUtils.trimToNull(settings.getGlobalIPv6Address());
    }
    return StringUtils.trimToNull(settings.getIpAddress());
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

}
