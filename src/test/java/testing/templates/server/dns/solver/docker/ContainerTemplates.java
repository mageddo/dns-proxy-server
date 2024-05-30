package testing.templates.server.dns.solver.docker;

import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.dnsproxyserver.solver.docker.application.DpsContainerService;
import com.mageddo.net.IP;
import com.mageddo.utils.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContainerTemplates {

  public static Container dpsContainer() {
    return builder()
      .preferredNetworkNames(Sets.ordered("shibata", "dps", "bridge"))
      .networks(Map.of(
        "dps", ContainerNetworkTemplates.build(DpsContainerService.DPS_CONTAINER_IP),
        "shibata", ContainerNetworkTemplates.build("172.23.0.2"),
        "bridge", ContainerNetworkTemplates.build("172.17.0.4")
      ))
      .ips(List.of(IP.of("172.17.0.5")))
      .build();
  }

  public static Container withDpsLabel() {
    return builder()
      .preferredNetworkNames(Sets.ordered("shibata", "dps", "bridge"))
      .networks(Map.of(
        "dps", ContainerNetworkTemplates.build("172.157.5.3"),
        "shibata", ContainerNetworkTemplates.build("172.23.0.2"),
        "bridge", ContainerNetworkTemplates.build("172.17.0.4")
      ))
      .ips(List.of(IP.of("172.17.0.5")))
      .build();
  }

  public static Container withDefaultBridgeNetworkOnly() {
    return builder()
      .networks(Map.of(
        "bridge", ContainerNetworkTemplates.build("172.17.0.4")
      ))
      .ips(List.of(IP.of("172.17.0.5")))
      .build();
  }

  public static Container withCustomBridgeAndOverlayNetwork() {
    return builder()
      .networks(Map.of(
        "shibata", ContainerNetworkTemplates.build("172.23.0.2"),
        "custom-bridge", ContainerNetworkTemplates.build("172.17.0.8")
      ))
      .build();
  }

  public static Container withIpv6DefaultBridgeNetworkOnly() {
    return builder()
      .networks(Map.of(
        "bridge", ContainerNetworkTemplates.build("172.17.0.4", "2001:db8:abc1::242:ac11:4")
      ))
      .ips(IP.listOf("172.17.0.4", "2001:db8:abc1::242:ac11:4"))
      .build();

  }

  public static Container withIpv6CustomBridgeNetwork() {
    return builder()
      .networks(Map.of(
        "my-net1", ContainerNetworkTemplates.build("172.21.0.2", "2001:db8:1::2")
      ))
      .build();
  }

  public static Container withDefaultIpv6Only() {
    return builder()
      .networks(Map.of(
        "my-net1", ContainerNetworkTemplates.build("172.21.0.2")
      ))
      .ips(IP.listOf("2001:db8:1:0:0:0:0:2"))
      .build();
  }


  public static Container withIpv4DefaultBridgeAndIpv6CustomBridgeNetwork() {
    return builder()
      .networks(Map.of(
        "bridge", ContainerNetworkTemplates.build("172.17.0.4"),
        "my-net1", ContainerNetworkTemplates.build("172.21.0.2", "2001:db8:1::2")
      ))
      .ips(Collections.emptyList())
      .build();
  }

  private static Container.ContainerBuilder builder() {
    return Container.builder()
      .id("ccb1becce0235218556b8de161d54383782f0ac6de5f83eff88d4c360068c536")
      .name("/laughing_swanson")
      .preferredNetworkNames(Sets.ordered("dps", "bridge"))
      ;
  }
}
