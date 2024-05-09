package com.mageddo.dnsproxyserver.dnsconfigurator;

import com.mageddo.dnsproxyserver.docker.DockerFacade;
import com.mageddo.dnsproxyserver.docker.DpsContainerManager;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DpsIpDiscover {

  private final DockerFacade dockerFacade;
  private final DpsContainerManager dpsContainerManager;

  public IP findDpsIP() {
    if (this.dpsContainerManager.isDpsRunningInsideContainer()) {
      return Optional
        .ofNullable(this.dpsContainerManager.findDpsContainerIP())
        .orElseGet(this.dockerFacade::findHostMachineIp);
    }
    return Networks.findCurrentMachineIP();
  }


}
