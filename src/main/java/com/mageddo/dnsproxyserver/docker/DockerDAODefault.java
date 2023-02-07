package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.dnsproxyserver.docker.DockerNetworks.NETWORK_BRIDGE;
import static com.mageddo.dnsproxyserver.docker.DockerNetworks.NETWORK_DPS;
import static com.mageddo.dnsproxyserver.docker.Labels.DEFAULT_NETWORK_LABEL;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAODefault implements DockerDAO {

  public static final String RUNNING_STATUS = "running";
  private final DockerClient dockerClient;

  @Override
  public String findBestHostIP(Hostname host) {

    final var stopWatch = StopWatch.createStarted();
    final var activeContainers = this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Set.of(RUNNING_STATUS))
      .withLimit(1024)
//      .withNetworkFilter()
      .exec();

    final var foundIp = activeContainers
      .stream()
      .map(it -> this.dockerClient.inspectContainerCmd(it.getId()).exec())
      .filter(ContainerHostnameMatcher.buildPredicate(host))
      .map(c -> DockerNetworks.findBestIpMatching(c, buildNetworks(c), this::findHostMachineIp))
      .findFirst()
      .orElse(null);
    log.debug("status=findDone, host={}, found={}, time={}", host, foundIp, stopWatch.getTime());
    return foundIp;
  }

  @Override
  public String findHostMachineIp() {
    return DockerNetworks.findIp(this.findBestNetwork());
  }

  @Override
  public boolean isConnected() {
    return true; // fixme implement the right logic
  }

  Network findBestNetwork() {
    return this.dockerClient.listNetworksCmd()
      .exec()
      .stream()
      .min(NetworkComparator::compare)
      .orElse(null);
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
