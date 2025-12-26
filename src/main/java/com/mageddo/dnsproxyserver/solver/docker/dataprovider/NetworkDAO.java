package com.mageddo.dnsproxyserver.solver.docker.dataprovider;


import java.util.function.Predicate;

import com.mageddo.dnsproxyserver.docker.NetworkConnectionStatus;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.solver.docker.Network;

public interface NetworkDAO {

  Network findById(String networkId);

  Network findByName(String networkName);

  boolean existsByName(String networkName);

  String findContainerWithNetworkAndIp(String networkName, String ip);

  void disconnect(String networkId, String containerId);

  NetworkConnectionStatus connect(String networkNameOrId, String containerId);

  void connect(String networkNameOrId, String containerId, String networkIp);

  void connectRunningContainersToNetwork(String networkName, Predicate<ContainerCompact> p);
}
