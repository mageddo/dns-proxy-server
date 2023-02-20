package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.quarkus.DockerConfig;
import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.os.linux.LinuxFiles;
import com.sun.jna.Platform;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAODefault implements DockerDAO {

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
    if (!Platform.isLinux()) {
      return false; // todo not supporting windows and mac for now
    }
    final var path = Paths.get(DockerConfig.DOCKER_HOST_ADDRESS.getPath());
    return Files.exists(path) && LinuxFiles.isUnixSocket(path);
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
