package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.utils.InetAddresses;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import java.util.List;

public class RemoteResolvers {

  private final List<Resolver> resolvers;

  public RemoteResolvers(List<Resolver> resolvers) {
    this.resolvers = resolvers;
  }

  public static RemoteResolvers of(List<IpAddr> servers) {
    final var resolvers = servers
      .stream()
      .map(it -> (Resolver) new SimpleResolver(InetAddresses.toSocketAddress(it)))
      .toList();
    return new RemoteResolvers(resolvers);
  }

  public List<Resolver> resolvers() {
    return this.resolvers;
  }
}
