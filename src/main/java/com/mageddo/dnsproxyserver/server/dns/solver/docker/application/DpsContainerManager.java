package com.mageddo.dnsproxyserver.server.dns.solver.docker.application;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.DockerNetworkDAO;
import com.mageddo.net.IP;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Map;

import static com.mageddo.dnsproxyserver.server.dns.solver.docker.Network.Name;

// fixme #444 - acoplado a infraestrutura
@Slf4j
@Default
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class DpsContainerManager {

  static final String DPS_INSIDE_CONTAINER = "1";

  private final DpsContainerService dpsContainerService;
  private final DockerClient dockerClient;
  private final DockerNetworkDAO dockerNetworkDAO;

  public void setupNetwork() {
    final var configureNetwork = BooleanUtils.isTrue(Configs.getInstance().getDpsNetwork());
    log.info("status=dpsNetwork, active={}", configureNetwork);
    if (!configureNetwork) {
      return;
    }
    this.createWhereNotExists();
    this.dpsContainerService.connectDpsContainer();
  }

  void createWhereNotExists() {
    if (this.dockerNetworkDAO.existsByName(Name.DPS.lowerCaseName())) {
      log.debug("status=dpsNetworkAlreadyExists");
      return;
    }
    final var currentVersion = Configs.getInstance().getVersion();
    final var res = this.dockerClient
      .createNetworkCmd()
      .withName(Name.DPS.lowerCaseName())
      .withDriver(Name.BRIDGE.lowerCaseName())
      .withCheckDuplicate(false)
      .withEnableIpv6(true)
      .withIpam(
        new Network.Ipam()
          .withConfig(
            new Network.Ipam.Config()
              .withSubnet("172.157.0.0/16")
              .withIpRange("172.157.5.3/24")
              .withGateway("172.157.5.1"),
            new Network.Ipam.Config()
              .withSubnet("fc00:5c6f:db50::/64")
              .withGateway("fc00:5c6f:db50::1")
          )
      )
      .withInternal(false)
      .withAttachable(true)
      .withLabels(Map.of(
        "description", "Dns Proxy Server Name: https://github.com/mageddo/dns-proxy-server",
        "version", currentVersion
      ))
      .exec();
    log.info("status=networkCreated, id={}, warnings={}", res.getId(), Arrays.toString(res.getWarnings()));
  }

  public IP findDpsContainerIP() {
    return this.dpsContainerService.findDpsContainerIP();
  }

  public boolean isDpsRunningInsideContainer() {
    return StringUtils.equals(getDpsContainerEnv(), DPS_INSIDE_CONTAINER);
  }

  String getDpsContainerEnv() {
    return System.getenv("DPS_CONTAINER");
  }

}
