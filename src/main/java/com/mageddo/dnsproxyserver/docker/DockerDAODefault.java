package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mageddo.dnsproxyserver.docker.Docker.findContainerHostname;
import static com.mageddo.dnsproxyserver.docker.Docker.findHostnameFromEnv;
import static com.mageddo.dnsproxyserver.docker.DockerNetworks.DEFAULT_NETWORK_LABEL;
import static com.mageddo.dnsproxyserver.docker.DockerNetworks.NETWORK_BRIDGE;
import static com.mageddo.dnsproxyserver.docker.DockerNetworks.NETWORK_DPS;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAODefault implements DockerDAO {

  public static final String RUNNING_STATUS = "running";
  private final DockerClient dockerClient;

  @Override
  public String findHostIp(Hostname host) {

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
      .filter(matchingHostName(host))
      .map(c -> DockerNetworks.findBestIpMatching(c, buildNetworks(c)))
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

  com.github.dockerjava.api.model.Network findBestNetwork() {
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

  static Predicate<InspectContainerResponse> matchingHostName(Hostname host) {
    return it -> {
      if (host.isEqualTo(findContainerHostname(it.getConfig()))) {
        return true;
      }
      return findHostnameFromEnv(it.getConfig().getEnv()).contains(host);

      // todo find hostname by container name or service name Config.registerContainerNames
      //      usar o Config.domain como dominio para o nome do service ou do container.

      // 	if conf.ShouldRegisterContainerNames() {
      //		hostnames = append(hostnames, getHostnameFromContainerName(inspect))
      //		if hostnameFromServiceName, err := getHostnameFromServiceName(inspect); err == nil {
      //			hostnames = append(hostnames, hostnameFromServiceName)
      //		}
      //	}
    };
  }
}
