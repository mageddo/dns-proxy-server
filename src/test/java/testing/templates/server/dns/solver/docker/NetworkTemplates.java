package testing.templates.server.dns.solver.docker;

import com.mageddo.dnsproxyserver.docker.domain.Drivers;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;
import com.mageddo.net.IP;

import java.util.Collections;

public class NetworkTemplates {
  public static Network withOverlayDriver(String name) {
    return builder()
      .name(name)
      .driver(Drivers.OVERLAY)
      .build()
      ;
  }

  public static Network withBridgeDriver(String name) {
    return builder()
      .name(name)
      .driver(Drivers.BRIDGE)
      .build()
      ;
  }

  public static Network withBridgeIpv4AndIpv6Network() {
    return builder()
      .name("my-net1")
      .driver(Drivers.BRIDGE)
      .gateways(IP.listOf("2001:db8:1::1", "172.21.0.1"))
      .build()
    ;
  }

  static Network.NetworkBuilder builder() {
    return Network.builder()
      .gateways(Collections.emptyList())
      ;
  }

}
