package com.mageddo.dnsproxyserver.solver;

import com.mageddo.net.IpAddr;
import com.mageddo.utils.Executors;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class RemoteResolvers implements AutoCloseable {

  private final List<Resolver> resolvers;
  private final ExecutorService executor;

  private RemoteResolvers(List<Resolver> resolvers, ExecutorService executor) {
    this.resolvers = resolvers;
    this.executor = executor;
  }

  public static RemoteResolvers of(List<IpAddr> servers, final Function<IpAddr, Resolver> resolverProvider) {
    final var resolvers = servers
      .stream()
      .map(resolverProvider)
      .toList();
    return new RemoteResolvers(resolvers, Executors.newThreadExecutor());
  }

  public List<Resolver> resolvers() {
    return this.resolvers;
  }

  @Override
  public void close() throws Exception {
    this.executor.close();
  }

  public Executor getExecutor() {
    return this.executor;
  }
}
