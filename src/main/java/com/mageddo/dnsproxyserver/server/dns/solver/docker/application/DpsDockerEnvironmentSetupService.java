package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.NetworkDAO;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DpsContainerDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.Network.Name;

@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
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
    return Configs.getInstance().getMustConfigureDpsNetwork();
  }

  void createNetworkIfAbsent() {
    if (this.networkDAO.existsByName(Name.DPS.lowerCaseName())) {
      log.debug("status=dpsNetworkAlreadyExists");
      return;
    }
    this.dpsContainerDAO.createDpsNetwork();
  }

}
