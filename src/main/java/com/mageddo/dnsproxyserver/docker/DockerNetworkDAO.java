package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Predicate;

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

  void connectRunningContainers(String networkName);

  void connectRunningContainers(String networkName, Predicate<Container> p);

  boolean exists(String networkId);

  boolean existsByName(String networkName);

}
