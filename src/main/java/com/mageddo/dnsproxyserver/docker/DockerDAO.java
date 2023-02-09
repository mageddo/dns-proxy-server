package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.server.dns.IP;

import java.util.List;

public interface DockerDAO {

  IP findHostMachineIp();

  boolean isConnected();

  List<Container> findActiveContainers();

  InspectContainerResponse inspect(String id);

  String findHostMachineIpRaw();

}
