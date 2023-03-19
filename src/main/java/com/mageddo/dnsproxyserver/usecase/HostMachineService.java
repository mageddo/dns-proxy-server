package com.mageddo.dnsproxyserver.usecase;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.IP;
import com.mageddo.net.Networks;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class HostMachineService {

  private final DockerDAO dockerDAO;

  @Inject
  public HostMachineService(DockerDAO dockerDAO) {
    this.dockerDAO = dockerDAO;
  }

  public IP findHostMachineIP() {
    return Optional
      .ofNullable(Networks.findCurrentMachineIP())
      .filter(it -> !it.isLoopback())
      .orElseGet(() -> {
        if (this.dockerDAO.isConnected()) {
          return this.dockerDAO.findHostMachineIp();
        }
        return null;
      });
  }
}
