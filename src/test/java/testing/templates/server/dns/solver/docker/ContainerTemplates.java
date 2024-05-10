package testing.templates.server.dns.solver.docker;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.net.IP;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContainerTemplates {
  public static Container withDpsLabel() {
    return builder()
      .build();
  }

  public static Container withDefaultBridgeNetworkOnly() {
    return builder()
      .networkNames(Set.of("bridge"))
      .networks(Map.of(
          "bridge", ContainerNetworkTemplates.build("172.17.0.4")
      ))
      .build();
  }

  private static Container.ContainerBuilder builder() {
    return Container.builder()
      .id("ccb1becce0235218556b8de161d54383782f0ac6de5f83eff88d4c360068c536")
      .name("/laughing_swanson")
      .networkNames(
        Stream.of("shibata", "dps", "bridge")
          .collect(Collectors.toCollection(LinkedHashSet::new))
      )
      .networks(Map.of(
        "dps", ContainerNetworkTemplates.build("172.157.5.3"),
        "shibata", ContainerNetworkTemplates.build("172.23.0.2"),
        "bridge", ContainerNetworkTemplates.build("172.17.0.4")
      ))
      .ips(List.of(IP.of("172.17.0.4")));
  }

}
