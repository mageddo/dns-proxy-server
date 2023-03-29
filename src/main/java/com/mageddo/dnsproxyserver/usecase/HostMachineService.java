package com.mageddo.dnsproxyserver.usecase;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.docker.DpsContainerManager;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class HostMachineService {

  private final DockerDAO dockerDAO;
  private final DpsContainerManager dpsContainerManager;

  public IP findHostMachineIP() {
    return this.findHostMachineIP(IP.Version.IPV4);
  }

  public IP findHostMachineIP(IP.Version version) {
    if (this.isDpsRunningInsideContainer()) {
      return this.dockerDAO.findHostMachineIp(version);
    }
    return this.findCurrentMachineIp(version);
  }

  IP findCurrentMachineIp() {
    return this.findCurrentMachineIp(IP.Version.IPV4);
  }

  IP findCurrentMachineIp(IP.Version version) {
    return Networks.findCurrentMachineIP(version);
  }

  boolean isDpsRunningInsideContainer() {
    return this.dpsContainerManager.isDpsRunningInsideContainer();
  }
}
