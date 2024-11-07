package com.mageddo.dnsproxyserver.docker.dataprovider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.application.Containers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerFacadeDefault implements ContainerFacade {

  private final DockerClient dockerClient;

  @Override
  public Container findById(String containerId) {
    return this.dockerClient.listContainersCmd()
      .withIdFilter(Collections.singleton(containerId))
      .exec()
      .stream()
      .findFirst()
      .orElse(null)
      ;
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
  public Optional<InspectContainerResponse> inspect(String id) {
    try {
      return Optional.of(this.dockerClient.inspectContainerCmd(id).exec());
    } catch (NotFoundException e) {
      log.warn("status=container-not-found, action=inspect-container, containerId={}", id);
    } catch (Exception e) {
      log.warn("status=unexpected-exception, action=inspect-container, containerId={}, msg={}", id, e.getMessage(), e);
    }

    return Optional.empty();
  }
}
