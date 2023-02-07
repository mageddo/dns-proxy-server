package com.mageddo.dnsproxyserver.docker;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.mageddo.dnsproxyserver.docker.domain.Network.BRIDGE;
import static com.mageddo.dnsproxyserver.docker.domain.Network.DPS;

@Slf4j
public class DockerNetworks {

  public static final String NETWORK_DPS = DPS.lowerName();
  public static final String NETWORK_BRIDGE = BRIDGE.lowerName();
  public static final String NETWORK_MODE_HOST = "host";

  public static String findBestIpMatching(
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

//    for (final var name : networks.keySet()) {
//      for (final var wantedNetwork : networksNames) {
//        if (name.endsWith(wantedNetwork)) {
//          log.debug("status=patternMached, network={}, with={}", name, wantedNetwork);
//          return networks.get(name).getIpAddress();
//        }
//      }
//    }

    return networks
      .keySet()
      .stream()
      .min(NetworkComparator::compare)
      .map(name -> {
        final var ip = networks.get(name).getIpAddress();
        log.debug("status=foundIp, network={}, ip={}", name, ip);
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
}
