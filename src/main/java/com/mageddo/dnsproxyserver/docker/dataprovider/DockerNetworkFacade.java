package com.mageddo.dnsproxyserver.docker.dataprovider;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface DockerNetworkFacade {

  Network findById(String id);

  Network findByName(String networkName);

  Pair<String, ContainerNetwork> findContainerWithIp(String networkName, String ip);

  List<Container> findNetworkContainers(String networkId);

  void disconnect(String networkId, String containerId);

  /**
   * Lenient network connect to a container.
   */
  NetworkConnectionStatus connect(String networkNameOrId, String containerId);

  void connect(String networkNameOrId, String containerId, String ip);

  boolean exists(String networkId);

}
