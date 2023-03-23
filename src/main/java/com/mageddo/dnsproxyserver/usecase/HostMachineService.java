package com.mageddo.dnsproxyserver.usecase;

import com.mageddo.dnsproxyserver.docker.DockerDAO;
import com.mageddo.dnsproxyserver.docker.DpsContainerManager;
import com.mageddo.net.IPI;
import com.mageddo.net.Networks;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class HostMachineService {

  private final DockerDAO dockerDAO;
  private final DpsContainerManager dpsContainerManager;

  public IPI findHostMachineIP() {
    if (this.isDpsRunningInsideContainer()) {
      return this.dockerDAO.findHostMachineIp();
    }
    return this.findCurrentMachineIp();
  }

  boolean isDpsRunningInsideContainer() {
    return this.dpsContainerManager.isDpsRunningInsideContainer();
  }

  IPI findCurrentMachineIp() {
    return Networks.findCurrentMachineIP();
  }
}
