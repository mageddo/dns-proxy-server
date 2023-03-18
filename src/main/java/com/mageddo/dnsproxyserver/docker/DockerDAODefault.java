package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.quarkus.DockerConfig;
import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.os.Platform;
import com.mageddo.os.linux.LinuxFiles;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerDAODefault implements DockerDAO {

  private final DockerClient dockerClient;

  @Override
  public IP findHostMachineIp() {
    return IP.of(this.findHostMachineIpRaw());
  }

  @Override
  public String findHostMachineIpRaw() {
    return DockerNetworkService.findIp(this.findBestNetwork());
  }

  @Override
  public boolean isConnected() {
    if (Platform.isLinux()) {
      final var path = Paths.get(DockerConfig.DOCKER_HOST_ADDRESS.getPath());
      return Files.exists(path) && LinuxFiles.isUnixSocket(path);
    } else if (Platform.isMac()) {
      final var path = Paths.get(DockerConfig.DOCKER_HOST_ADDRESS.getPath());
      return Files.exists(path) && !Files.isDirectory(path) && Files.isReadable(path);
    }
    log.trace("docker features still not supported on this platform :/ , hold tight I'm working hard to fix it someday :D");
    return false; // todo support all plataforms...
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
    final var network = this.dockerClient.listNetworksCmd()
      .exec()
      .stream()
      .min(NetworkComparator::compare)
      .orElse(null);
    log.debug(
      "status=bestNetwork, network={}, ip={}",
      Objects.mapOrNull(network, Network::getName),
      DockerNetworkService.findIp(network)
    );
    return network;
  }


}
