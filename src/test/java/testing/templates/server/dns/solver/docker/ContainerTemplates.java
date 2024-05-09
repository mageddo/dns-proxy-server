package testing.templates.server.dns.solver.docker;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;

public class ContainerTemplates {
  public static Container withDpsLabel() {
    return Container.builder()
      .id("ccb1becce0235218556b8de161d54383782f0ac6de5f83eff88d4c360068c536")
//      . "dps.network": "shibata",
      .build();
  }
}
