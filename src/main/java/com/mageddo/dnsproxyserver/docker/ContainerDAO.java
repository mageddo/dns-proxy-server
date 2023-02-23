package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;

import java.util.List;

public interface ContainerDAO {
  List<Container> findNetworkContainers(String networkId);
}
