package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
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

  private final DpsContainerService dpsContainerService;
  private final DockerNetworkDAO dockerNetworkDAO;
  public final DpsContainerDAO dpsContainerDAO;

  public void setup() {
    this.setupNetwork();
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
    if (this.dockerNetworkDAO.existsByName(Name.DPS.lowerCaseName())) {
      log.debug("status=dpsNetworkAlreadyExists");
      return;
    }
    this.dpsContainerDAO.createDpsNetwork();
  }

}
