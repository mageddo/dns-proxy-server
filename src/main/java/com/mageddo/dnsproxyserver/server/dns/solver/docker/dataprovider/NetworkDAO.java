package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;


import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.domain.NetworkConnectionStatus;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;

import java.util.function.Predicate;

public interface NetworkDAO {

  Network findById(String networkId);

  Network findByName(String networkName);

  boolean existsByName(String networkName);

  String findContainerWithNetworkAndIp(String networkName, String ip);

  void disconnect(String networkId, String containerId);

  NetworkConnectionStatus connect(String networkNameOrId, String containerId);

  void connect(String networkNameOrId, String containerId, String networkIp);

  void connectRunningContainersToNetwork(String networkName, Predicate<Container> p); // fixme #444 remove infrastructure dep
}
