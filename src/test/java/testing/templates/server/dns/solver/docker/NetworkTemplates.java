package testing.templates.server.dns.solver.docker;

import com.mageddo.dnsproxyserver.docker.domain.Drivers;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.Network;

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

  static Network.NetworkBuilder builder() {
    return Network.builder()
      .gateways(Collections.emptyList())
      ;
  }
}
