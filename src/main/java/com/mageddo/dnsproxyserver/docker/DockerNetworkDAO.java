package com.mageddo.dnsproxyserver.docker;

import java.util.List;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Network;

import org.apache.commons.lang3.tuple.Pair;

public interface DockerNetworkDAO {

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
