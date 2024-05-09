package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;

import java.util.List;

public interface ContainerFacade {

  List<Container> findNetworkContainers(String networkId);

  Container findById(String containerId);
}
