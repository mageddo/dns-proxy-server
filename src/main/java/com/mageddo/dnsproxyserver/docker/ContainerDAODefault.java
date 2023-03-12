package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Slf4j
@Default
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ContainerDAODefault implements ContainerDAO {

  private final DockerClient dockerClient;

  @Override
  public List<Container> findNetworkContainers(String networkId) {
    return this.dockerClient.listContainersCmd()
      .withNetworkFilter(List.of(networkId))
      .exec();
  }

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
}
