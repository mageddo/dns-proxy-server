package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Entry;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.ContainerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import com.mageddo.net.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.mageddo.commons.lang.Objects.mapOrNull;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerSolvingService {

  private final DockerNetworkDAO dockerNetworkDAO;
  private final DockerDAO dockerDAO;
  private final ContainerDAO containerDAO;

  public Entry findBestMatch(HostnameQuery host) {
    final var stopWatch = StopWatch.createStarted();
    final var matchedContainers = this.containerDAO.findActiveContainersInspectMatching(host);
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

  public String findBestIpMatch(Container c) {
    return this.findBestIpMatch(c, IP.Version.IPV4);
  }

  public String findBestIpMatch(Container c, IP.Version version) {
    // todo #444 move to another method findAtPreferredNetworks
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
      "status=predefinedNetworkNotFound, action=findSecondOption, searchedNetworks={}, container={}",
      preferredNetworkNames, c.getName()
    );

    // todo #444 extract to another method, findAtAvailableNetworks
    return networks
      .keySet()
      .stream()
      .map(nId -> {
        final var network = this.dockerNetworkDAO.findByName(nId);
        if (network == null) {
          log.warn("status=networkIsNull, id={}", nId);
        }
        return network;
      })
      .filter(Objects::nonNull)
      .min(NetworkComparator::compare)
      .map(network -> {
        final var networkName = network.getName();
        final var ip = networks.get(networkName).getIpAsText(version);
        log.debug(
          "status=foundIp, networks={}, networkName={}, driver={}, foundIp={}",
          networks.keySet(), networkName, network.getDriver(), ip
        );
        return StringUtils.trimToNull(ip);
      })
      .filter(StringUtils::isNotBlank)
      .orElseGet(() -> {
        return Optional
          .ofNullable(mapOrNull(c.geDefaultIp(version), IP::toText))
          .orElseGet(() -> buildHostMachineIpWhenActive(version));
      })
      ;
  }

  String buildHostMachineIpWhenActive(IP.Version version) {
    if(isDockerSolverHostMachineFallbackActive()){
      final Supplier<String> hostMachineSup = () -> mapOrNull(this.dockerDAO.findHostMachineIp(version), IP::toText);
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
}
