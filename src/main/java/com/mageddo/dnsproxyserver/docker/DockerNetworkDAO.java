package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Network;
import org.apache.commons.lang3.tuple.Pair;

public interface DockerNetworkDAO {

  Network findNetwork(String id);

  Network findByName(String networkName);

  Pair<String, Network.ContainerNetworkConfig> findContainerWithIp(String networName, String ip);

  void disconnect(String networkId, String containerId);

  void connect(String networkNameOrId, String containerId);

  void connect(String networkNameOrId, String containerId, String ip);

  void connectRunningContainers(String networkName);

  boolean exists(String networkId);

  boolean existsByName(String networkName);

}
