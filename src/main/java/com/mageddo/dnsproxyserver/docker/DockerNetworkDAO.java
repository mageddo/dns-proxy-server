package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.model.Network;
import org.apache.commons.lang3.tuple.Pair;

public interface DockerNetworkDAO {

  Network findNetwork(String id);

  Pair<String, Network.ContainerNetworkConfig> findContainerWithIp(String networkId, String ip);

  void disconnect(String networkId, String containerId);

  void connect(String networkId, String containerId);

  void connet(String networkId, String containerId, String ip);
}
