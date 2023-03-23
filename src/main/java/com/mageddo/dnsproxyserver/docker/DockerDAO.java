package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.mageddo.net.IPI;

import java.util.List;

public interface DockerDAO {

  IPI findHostMachineIp();

  boolean isConnected();

  List<Container> findActiveContainers();

  InspectContainerResponse inspect(String id);

  String findHostMachineIpRaw();

}
