package com.mageddo.dnsproxyserver.docker.dataprovider;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;

import java.util.List;
import java.util.Optional;

public interface ContainerFacade {

  Container findById(String containerId);

  List<Container> findActiveContainers();

  Optional<InspectContainerResponse> inspect(String id);
}
