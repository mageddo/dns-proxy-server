package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;


import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;

import java.util.function.Predicate;

public interface DockerNetworkDAO {

  Network findById(String networkId);

  boolean existsByName(String networkName);

  String findContainerWithNetworkAndIp(String networkName, String ip);

  void disconnect(String networkId, String containerId);

  void connect(String networkNameOrId, String containerId);

  void connect(String networkNameOrId, String containerId, String networkIp);

  void connectRunningContainers(String networkName, Predicate<Container> p);
}
