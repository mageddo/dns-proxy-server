package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.mageddo.net.IP;

import java.util.List;

public interface DockerDAO {

  boolean isConnected();

  List<Container> findActiveContainers();

  InspectContainerResponse inspect(String id);

  IP findHostMachineIp();

  IP findHostMachineIp(IP.Version version);

}
