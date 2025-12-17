package testing.templates.server.dns.solver.docker;

import java.util.stream.Stream;

import com.mageddo.dnsproxyserver.solver.docker.Container;
import com.mageddo.net.IP;

public class ContainerNetworkTemplates {
  public static Container.Network build(String... ips) {
    return Container.Network.builder()
        .ips(Stream.of(ips)
            .map(IP::of)
            .toList())
        .build();
  }
}
