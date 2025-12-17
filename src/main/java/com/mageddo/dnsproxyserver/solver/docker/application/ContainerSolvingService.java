package com.mageddo.dnsproxyserver.solver.docker.application;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.Entry;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.ContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAO;
import com.mageddo.net.IP;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.mageddo.commons.lang.Objects.mapOrNull;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class ContainerSolvingService {

  private final NetworkDAO networkDAO;
  private final DockerDAO dockerDAO;
  private final ContainerDAO containerDAO;

  public Entry findBestMatch(HostnameQuery query) {
    final var stopWatch = StopWatch.createStarted();
    final var matchedContainers = this.containerDAO.findActiveContainersMatching(query);
    final var foundIp = matchedContainers
        .stream()
        .map(it -> this.findBestIpMatch(it, query.getVersion()))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
    final var hostnameMatched = !matchedContainers.isEmpty() && foundIp != null;
    log.trace(
        "status=findDone, query={}, found={}, hostnameMatched={}, time={}",
        query, foundIp, hostnameMatched, stopWatch.getTime()
    );
    return Entry
        .builder()
        .hostnameMatched(hostnameMatched)
        .ip(IP.of(foundIp))
        .build();
  }

  public String findBestIpMatch(Container c) {
    return this.findBestIpMatch(c, IP.Version.IPV4);
  }

  public String findBestIpMatch(Container c, IP.Version version) {
    return Optional
        .ofNullable(this.findAtPreferredNetworks(c, version))
        .or(() -> this.findAtAvailableNetworksOptional(c, version))
        .orElseGet(() -> this.findSecondaryIp(c, version))
        ;
  }

  String findAtPreferredNetworks(Container c, IP.Version version) {
    final var networks = c.getNetworks();
    final var preferredNetworkNames = c.getPreferredNetworkNames();
    for (final var name : preferredNetworkNames) {
      if (!networks.containsKey(name)) {
        log.debug("status=networkNotFoundForContainer, name={}", name);
        continue;
      }
      final var containerNetwork = networks.get(name);
      final String ip = containerNetwork.getIpAsText(version);
      log.debug("status=foundIp, network={}, container={}, ip={}", name, c.getName(), ip);
      if (StringUtils.isNotBlank(ip)) {
        return ip;
      }
    }
    log.debug(
        "status=predefinedNetworkNotFound, action=findSecondOption, searchedNetworks={}, "
            + "container={}",
        preferredNetworkNames, c.getName()
    );
    return null;
  }

  Optional<String> findAtAvailableNetworksOptional(Container c, IP.Version version) {
    final var containerNetworks = c.getNetworks();
    return containerNetworks
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
        .map(network -> findContainerNetworkIp(containerNetworks, network, version))
        .filter(StringUtils::isNotBlank)
        ;
  }

  String findSecondaryIp(Container c, IP.Version version) {
    return Optional
        .ofNullable(mapOrNull(c.geDefaultIp(version), IP::toText))
        .orElseGet(() -> buildHostMachineIpWhenActive(version));
  }

  static String findContainerNetworkIp(
      Map<String, Container.Network> containerNetworks, Network network, IP.Version version
  ) {
    final var networkName = network.getName();
    final var ip = containerNetworks.get(networkName)
        .getIpAsText(version);
    log.debug(
        "status=foundIp, containerNetworks={}, networkName={}, driver={}, foundIp={}",
        containerNetworks.keySet(), networkName, network.getDriver(), ip
    );
    return StringUtils.trimToNull(ip);
  }

  String buildHostMachineIpWhenActive(IP.Version version) {
    if (isDockerSolverHostMachineFallbackActive()) {
      final Supplier<String> hostMachineSup = () -> mapOrNull(
          this.dockerDAO.findHostMachineIp(version), IP::toText);
      final var hostIp = hostMachineSup.get();
      log.debug("status=noNetworkAvailable, usingHostMachineIp={}", hostIp);
      return hostIp;
    }
    log.debug("dockerSolverHostMachineFallback=inactive");
    return null;
  }

  boolean isDockerSolverHostMachineFallbackActive() {
    return Configs.getInstance()
        .getDockerSolverHostMachineFallbackActive();
  }
}
