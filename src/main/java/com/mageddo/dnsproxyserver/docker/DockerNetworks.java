package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Network;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static com.mageddo.dnsproxyserver.docker.domain.Network.BRIDGE;
import static com.mageddo.dnsproxyserver.docker.domain.Network.DPS;

@Slf4j
public class DockerNetworks {

  public static final String NETWORK_DPS = DPS.lowerName();
  public static final String NETWORK_BRIDGE = BRIDGE.lowerName();

  public static final String DEFAULT_NETWORK_LABEL = "dps.network";

  public static String findBestIpMatching(InspectContainerResponse c, Collection<String> networksNames) {
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
    return c
      .getNetworkSettings()
      .getIpAddress();
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
}
