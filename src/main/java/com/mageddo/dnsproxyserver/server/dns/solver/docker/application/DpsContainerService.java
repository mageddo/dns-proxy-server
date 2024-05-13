package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DpsContainerDAO;
import com.mageddo.net.IP;
import com.mageddo.net.Networks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DpsContainerService {

  private final DockerDAO dockerDAO;
  private final DpsContainerDAO dpsContainerDAO;
  private final DockerNetworkDAO dockerNetworkDAO;
  private final ContainerSolvingService containerSolvingService;

  public IP findDpsContainerIP() {

    final var container = this.dpsContainerDAO.findDPSContainer();
    if (container == null) {
      log.debug("status=no-dps-container-found");
      return null;
    }

    final var ip = this.containerSolvingService.findBestIpMatch(container);
    if (StringUtils.isBlank(ip)) {
      return null;
    }
    return IP.of(ip);
  }

  public void connectDpsContainerTpItsNetwork() {
    final var container = this.dpsContainerDAO.findDPSContainer();
    if (container == null) {
      log.info("status=dps-container-not-found");
      return;
    }
    final var dpsContainerIP = "172.157.5.249";
    this.disconnectAnotherContainerWithSameIPFromDpsNetwork(container.getId(), dpsContainerIP);
    this.connectDpsContainerToDpsNetwork(container, dpsContainerIP);
  }

  void disconnectAnotherContainerWithSameIPFromDpsNetwork(String containerId, String ip) {
    final var cId = this.dockerNetworkDAO.findContainerWithNetworkAndIp(Network.Name.DPS.lowerCaseName(), ip);
    if (cId != null && !Objects.equals(containerId, cId)) {
      log.info(
        "status=detachingContainerUsingDPSIpFromDpsNetwork, ip={}, oldContainerId={}, newContainerId={}",
        ip, containerId, cId
      );
      this.dockerNetworkDAO.disconnect(Network.Name.DPS.lowerCaseName(), cId);
    }
  }

  void connectDpsContainerToDpsNetwork(Container container, String ip) {
    final var foundIp = container.geDefaultIp(IP.Version.IPV4, Network.Name.DPS.lowerCaseName());
    if (foundIp == null) {
      this.dockerNetworkDAO.connect(Network.Name.DPS.lowerCaseName(), container.getId());
      log.info("status=dpsContainerConnectedToDpsNetwork, containerId={}, ip={}", container.getId(), ip);
    } else if (foundIp.notEqualTo(ip)) {
      this.dockerNetworkDAO.disconnect(Network.Name.DPS.lowerCaseName(), container.getId());
      this.dockerNetworkDAO.connect(Network.Name.DPS.lowerCaseName(), container.getId(), ip);
      log.info(
        "status=dpsWasConnectedWithWrongIp, action=fixing, foundIp={}, rightIp={}, container={}",
        foundIp, ip, container.getId()
      );
    } else {
      log.debug("status=dpsContainerAlreadyConnectedToDpsNetwork, container={}", container.getId());
    }
  }

  public IP findDpsIP() {
    if (this.dpsContainerDAO.isDpsRunningInsideContainer()) {
      return Optional
        .ofNullable(this.findDpsContainerIP())
        .orElseGet(this.dockerDAO::findHostMachineIp);
    }
    return Networks.findCurrentMachineIP();
  }
}
