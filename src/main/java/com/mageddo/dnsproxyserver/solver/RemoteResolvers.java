package com.mageddo.dnsproxyserver.solver;

import java.util.List;
import java.util.function.Function;

import com.mageddo.net.IpAddr;

public class RemoteResolvers {

  private final List<Resolver> resolvers;

  private RemoteResolvers(List<Resolver> resolvers) {
    this.resolvers = resolvers;
  }

  public static RemoteResolvers of(List<IpAddr> servers,
      final Function<IpAddr, Resolver> resolverProvider) {
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
