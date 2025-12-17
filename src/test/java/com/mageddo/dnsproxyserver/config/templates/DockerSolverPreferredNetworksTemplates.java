package com.mageddo.dnsproxyserver.config.templates;

import java.util.List;

import com.mageddo.dnsproxyserver.config.Config.SolverDocker.Networks.Preferred;

public class DockerSolverPreferredNetworksTemplates {
  public static Preferred batataPreferredNetworkOverride() {
    return Preferred.builder()
        .names(List.of("batata"))
        .overrideDefault(true)
        .build();
  }

  public static Preferred batataPreferredNetwork() {
    return Preferred.builder()
        .names(List.of("batata"))
        .overrideDefault(false)
        .build();
  }
}
