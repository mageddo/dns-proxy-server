package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerFacadeDefault implements DockerFacade {

  private final DockerClient dockerClient;

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




}
