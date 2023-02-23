package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.mageddo.dnsproxyserver.docker.domain.Network.BRIDGE;
import static com.mageddo.dnsproxyserver.docker.domain.Network.DPS;
import static com.mageddo.dnsproxyserver.docker.domain.Network.HOST;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DockerNetworkService {

  public static final String NETWORK_DPS = DPS.lowerCaseName();
  public static final String NETWORK_BRIDGE = BRIDGE.lowerCaseName();
  public static final String NETWORK_MODE_HOST = HOST.lowerCaseName();

  private final DockerNetworkDAO networkDAO;

  public String findBestIpMatch(
    InspectContainerResponse c, Collection<String> networksNames, Supplier<String> hostMachineSup
  ) {

    final var networks = c
      .getNetworkSettings()
      .getNetworks();

    for (final var name : networksNames) {
      if (!networks.containsKey(name)) {
        log.debug("status=networkNotFoundForContainer, name={}", name);
        continue;
      }
      final var ip = networks.get(name).getIpAddress();
      log.debug("status=foundIp, network={}, container={}, ip={}", name, c.getName(), ip);
      return ip;
    }
    log.debug(
      "status=predefinedNetworkNotFound, action=findSecondOption, searchedNetworks={}, container={}",
      networksNames, c.getName()
    );

    return networks
      .keySet()
      .stream()
      .map(this.networkDAO::findNetwork)
      .min(NetworkComparator::compare)
      .map(network -> {
        final var name = network.getName();
        final var ip = networks.get(name).getIpAddress();
        log.debug("status=foundIp, network={}, driver={}, ip={}", name, network.getDriver(), ip);
        return StringUtils.trimToNull(ip);
      })
      .filter(StringUtils::isNotBlank)
      .orElseGet(() -> {
        return Optional
          .ofNullable(buildDefaultIp(c))
          .orElseGet(() -> {
            final var hostIp = hostMachineSup.get();
            log.debug("status=noNetworkAvailable, usingHostMachineIp={}", hostIp);
            return hostIp;
          })
          ;
      })
      ;

  }

  static String buildDefaultIp(InspectContainerResponse c) {
    return StringUtils.trimToNull(c
      .getNetworkSettings()
      .getIpAddress()
    );
  }

  public static String findIp(Network network) {
    if (network == null) {
      return null;
    }
    return network
      .getIpam()
      .getConfig()
      .get(0)
      .getGateway();
  }

  public static Boolean isHostNetwork(Container container) {
    final var config = container.getHostConfig();
    if (config == null) {
      return null;
    }
    final var networkMode = config.getNetworkMode();
    return Objects.equals(networkMode, NETWORK_MODE_HOST);
  }

  public List<String> disconnect(String id) {
    final var containers = new ArrayList<String>();
    final var network = this.networkDAO.findNetwork(id);
    if (network == null) {
      return null;
    }
    final var container = network.getContainers();
    for (final var containerId : container.keySet()) {
      this.networkDAO.disconnect(id, containerId);
      containers.add(containerId);
    }
    return containers;
  }
}
