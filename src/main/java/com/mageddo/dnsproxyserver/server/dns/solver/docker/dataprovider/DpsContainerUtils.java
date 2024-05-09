package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider;

import com.github.dockerjava.api.model.Container;

import java.util.Objects;

public class DpsContainerUtils {

  public static boolean isDpsContainer(Container c) {
    final var lbl = c.getLabels().get("dps.container");
    return Objects.equals(lbl, "true");
  }

  public static boolean isNotDpsContainer(Container container) {
    return !isDpsContainer(container);
  }

}
