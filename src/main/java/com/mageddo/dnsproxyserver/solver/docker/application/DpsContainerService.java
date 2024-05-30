package com.mageddo.dnsproxyserver.solver.docker.application;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.ContainerCompact;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAO;
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

  public static final String DPS_CONTAINER_IP = "172.157.5.249";

  private final DockerDAO dockerDAO;
  private final DpsContainerDAO dpsContainerDAO;
  private final NetworkDAO networkDAO;
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

  public void connectDpsContainerToDpsNetwork() {
    final var container = this.dpsContainerDAO.findDPSContainer();
    if (container == null) {
      log.info("status=dps-container-not-found");
      return;
    }
    this.disconnectAnotherContainerWithSameIPFromDpsNetwork(container.getId(), DPS_CONTAINER_IP);
    this.connectDpsContainerToDpsNetwork(container);
  }

  void connectDpsContainerToDpsNetwork(Container container) {
    final var containerIpAtDpsNetwork = container.getNetworkIp(IP.Version.IPV4, Network.Name.DPS.lowerCaseName());
    if (containerIpAtDpsNetwork == null) {
      this.networkDAO.connect(Network.Name.DPS.lowerCaseName(), container.getId());
      log.info("status=dpsContainerConnectedToDpsNetwork, containerId={}, ip={}", container.getId(), DPS_CONTAINER_IP);
    } else if (containerIpAtDpsNetwork.notEqualTo(DPS_CONTAINER_IP)) {
      this.fixDpsContainerIpAtDpsNetwork(container, containerIpAtDpsNetwork);
    } else {
      log.debug("status=dpsContainerAlreadyConnectedToDpsNetwork, container={}", container.getId());
    }
  }

  void fixDpsContainerIpAtDpsNetwork(Container container, IP containerIpAtDpsNetwork) {
    this.networkDAO.disconnect(Network.Name.DPS.lowerCaseName(), container.getId());
    this.networkDAO.connect(Network.Name.DPS.lowerCaseName(), container.getId(), DPS_CONTAINER_IP);
    log.info(
      "status=dpsWasConnectedWithWrongIp, action=fixing, containerIpAtDpsNetwork={}, correctIp={}, container={}",
      containerIpAtDpsNetwork, DPS_CONTAINER_IP, container.getId()
    );
  }

  void disconnectAnotherContainerWithSameIPFromDpsNetwork(String containerId, String ip) {
    final var cId = this.networkDAO.findContainerWithNetworkAndIp(Network.Name.DPS.lowerCaseName(), ip);
    if (cId != null && !Objects.equals(containerId, cId)) {
      log.info(
        "status=detachingContainerUsingDPSIpFromDpsNetwork, ip={}, oldContainerId={}, newContainerId={}",
        ip, containerId, cId
      );
      this.networkDAO.disconnect(Network.Name.DPS.lowerCaseName(), cId);
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

  public boolean connectRunningContainersToDpsNetwork(){
    final var config = Configs.getInstance();
    if (!config.getMustConfigureDpsNetwork() || !config.getDpsNetworkAutoConnect()) {
      log.info(
        "status=autoConnectDpsNetworkDisabled, dpsNetwork={}, dpsNetworkAutoConnect={}",
        config.getMustConfigureDpsNetwork(), config.getDpsNetworkAutoConnect()
      );
      return false;
    }
    this.networkDAO.connectRunningContainersToNetwork(
      Network.Name.DPS.lowerCaseName(), ContainerCompact::isNotDpsContainer
    );
    return true;
  }
}
