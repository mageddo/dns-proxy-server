package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.remote.ResolverStats;
import com.mageddo.dnsproxyserver.solver.remote.mapper.ResolverMapper;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResolverStatsFactory {

  private final CircuitBreakerService circuitBreakerService;
  private final RemoteResolvers remoteResolvers;

  public List<ResolverStats> find() {
    return this.remoteResolvers.resolvers()
      .stream()
      .map(this::find)
      .toList();
  }

  public List<Resolver> findResolversWithNonOpenCircuit() {
    return this.find()
      .stream()
      .map(ResolverStats::getResolver)
      .toList()
      ;
  }

  public ResolverStats find(Resolver resolver) {
    return ResolverMapper.toResolverStats(resolver, this.circuitBreakerService.getCircuitStatus(resolver.getAddress()));
  }
}
