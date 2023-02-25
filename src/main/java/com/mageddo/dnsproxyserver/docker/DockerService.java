package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.dnsproxyserver.docker.DockerNetworkService.NETWORK_BRIDGE;
import static com.mageddo.dnsproxyserver.docker.DockerNetworkService.NETWORK_DPS;
import static com.mageddo.dnsproxyserver.docker.Labels.DEFAULT_NETWORK_LABEL;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
// todo rename to ContainerSolvingService
public class DockerService {

  private final DockerDAO dockerDAO;
  private final DockerNetworkService dockerNetworkService;

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
    return this.dockerNetworkService.findBestIpMatch(inspect, buildNetworks(inspect), this.dockerDAO::findHostMachineIpRaw);
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
