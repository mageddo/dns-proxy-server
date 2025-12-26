package com.mageddo.dnsproxyserver.docker.application;

import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.solver.docker.Label;

public class ContainerPredicates {

  public static boolean isEnabledForDPS(Container c) {
    return Labels.findBoolean(c, Label.DPS_CONTAINER_ENABLED, true);
  }

}
