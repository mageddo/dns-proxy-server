package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.net.IpAddr;
import org.xbill.DNS.Resolver;

import java.util.List;
import java.util.function.Function;

public class RemoteResolvers {

  private final List<Resolver> resolvers;

  public RemoteResolvers(List<Resolver> resolvers) {
    this.resolvers = resolvers;
  }

  public static RemoteResolvers of(List<IpAddr> servers, final Function<IpAddr, Resolver> resolverProvider) {
    final var resolvers = servers
      .stream()
      .map(resolverProvider)
      .toList();
    return new RemoteResolvers(resolvers);
  }

  public List<Resolver> resolvers() {
    return this.resolvers;
  }
}
