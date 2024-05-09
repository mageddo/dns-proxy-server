package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;

import java.util.List;

public interface DockerFacade {

  List<Container> findActiveContainers();

  InspectContainerResponse inspect(String id);

}
