package com.mageddo.dnsproxyserver.docker.application;

import com.github.dockerjava.api.model.Container;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Containers {

  public static final Collection<String> RUNNING_STATUS_LIST = Collections.singletonList("running");

  public static String toNames(List<Container> containers) {
    return containers
      .stream()
      .map(Containers::firstNameOrId)
      .collect(Collectors.joining(", "));
  }

  public static String firstNameOrId(Container c) {
    return Stream.of(c.getNames())
      .findFirst()
      .orElse(c.getId())
      ;
  }

  public static boolean containsNetworkName(Container container, String networkName) {
    final var settings = container.getNetworkSettings();
    if (settings == null) {
      return false;
    }
    return settings
      .getNetworks()
      .containsKey(networkName);
  }
}
