package com.mageddo.dnsproxyserver.docker;

import java.util.function.Predicate;

import com.github.dockerjava.api.model.Container;
import com.mageddo.dnsproxyserver.docker.application.Labels;
import com.mageddo.dnsproxyserver.solver.docker.Label;

public class ContainerPredicates {

  public static Predicate<Container> isEnabledForDPS() {
    return c -> Labels.findBoolean(c, Label.DPS_CONTAINER_ENABLED, true);
  }

}
