package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.server.dns.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAODefault implements DockerDAO {

  public static final String LINUX_DISCONNECTED_ERROR = "LastErrorException: [2] No such file or directory";
  private final DockerClient dockerClient;

  @Override
  public IP findHostMachineIp() {
    return IP.of(this.findHostMachineIpRaw());
  }

  @Override
  public String findHostMachineIpRaw() {
    return DockerNetworks.findIp(this.findBestNetwork());
  }

  @Override
  public boolean isConnected() {
    try {
      this.dockerClient
        .versionCmd()
        .exec();
      return true;
    } catch (Throwable e) {
      final var knownError = ExceptionUtils.getRootCauseMessage(e)
        .contains(LINUX_DISCONNECTED_ERROR);
      if (!knownError) {
        log.warn("status=cant-connect-to-dockerm msg={}", e.getMessage(), e);
      }
      return false;
    }
  }

  @Override
  public List<Container> findActiveContainers() {
    final var activeContainers = this.dockerClient
      .listContainersCmd()
      .withStatusFilter(Containers.RUNNING_STATUS_LIST)
      .withLimit(1024)
//      .withNetworkFilter()
      .exec();
    return activeContainers;
  }

  @Override
  public InspectContainerResponse inspect(String id) {
    return this.dockerClient.inspectContainerCmd(id).exec();
  }

  Network findBestNetwork() {
    return this.dockerClient.listNetworksCmd()
      .exec()
      .stream()
      .min(NetworkComparator::compare)
      .orElse(null);
  }


}
