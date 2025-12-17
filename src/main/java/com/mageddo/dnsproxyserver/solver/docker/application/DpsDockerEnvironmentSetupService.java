package com.mageddo.dnsproxyserver.solver.docker.application;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.docker.Network;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerDAO;
import com.mageddo.dnsproxyserver.solver.docker.dataprovider.NetworkDAO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class DpsDockerEnvironmentSetupService {

  private final DockerDAO dockerDAO;
  private final DpsContainerService dpsContainerService;
  private final NetworkDAO networkDAO;
  public final DpsContainerDAO dpsContainerDAO;

  /**
   * @return true if connected all running containers to DPS network.
   */
  public boolean setup() {

    final var connectedToDocker = this.dockerDAO.isConnected();
    log.info("status=binding-docker-events, connectedToDocker={}", connectedToDocker);
    if (!connectedToDocker) {
      return false;
    }

    this.setupNetwork();

    return this.dpsContainerService.connectRunningContainersToDpsNetwork();
  }

  void setupNetwork() {
    final var configureNetwork = this.isMustConfigureDpsNetwork();
    log.info("status=dpsNetwork, active={}", configureNetwork);
    if (!configureNetwork) {
      return;
    }
    this.createNetworkIfAbsent();
    this.dpsContainerService.connectDpsContainerToDpsNetwork();
  }

  boolean isMustConfigureDpsNetwork() {
    return Configs.getInstance()
        .getDockerSolverMustConfigureDpsNetwork();
  }

  void createNetworkIfAbsent() {
    if (this.networkDAO.existsByName(Network.Name.DPS.lowerCaseName())) {
      log.debug("status=dpsNetworkAlreadyExists");
      return;
    }
    this.dpsContainerDAO.createDpsNetwork();
  }

}
