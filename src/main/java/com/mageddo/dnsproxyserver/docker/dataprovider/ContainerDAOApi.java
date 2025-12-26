package com.mageddo.dnsproxyserver.docker.dataprovider;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.ContainerDAO;
import com.mageddo.dnsproxyserver.docker.application.Containers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ContainerDAOApi implements ContainerDAO {

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
    return this.dockerClient
        .listContainersCmd()
        .withStatusFilter(Containers.RUNNING_STATUS_LIST)
        .withLimit(1024)
//      .withNetworkFilter()
        .exec();
  }

  @Override
  public InspectContainerResponse inspect(String id) {
    return this.dockerClient.inspectContainerCmd(id)
        .exec();
  }

  @Override
  public InspectContainerResponse safeInspect(String id) {
    try {
      return this.inspect(id);
    } catch (NotFoundException e) {
      log.warn("status=container-not-found, action=inspect-container, containerId={}", id);
    } catch (Exception e) {
      log.warn("status=unexpected-exception, action=inspect-container, containerId={}, msg={}", id,
          e.getMessage(), e
      );
    }
    return null;
  }

  @Override
  public Stream<InspectContainerResponse> inspectFilteringValidContainers(List<Container> containers) {
    return containers
        .stream()
        .map(Container::getId)
        .map(this::safeInspect)
        .filter(Objects::nonNull)
        ;
  }

}
