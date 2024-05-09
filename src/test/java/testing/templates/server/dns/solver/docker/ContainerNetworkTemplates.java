package testing.templates.server.dns.solver.docker;

import com.mageddo.dnsproxyserver.server.dns.solver.docker.Container;
import com.mageddo.net.IP;

import java.util.stream.Stream;

public class ContainerNetworkTemplates {
  public static Container.Network build(String ... ips) {
    return Container.Network.builder()
      .ips(Stream.of(ips).map(IP::of).toList())
      .build();
  }
}
