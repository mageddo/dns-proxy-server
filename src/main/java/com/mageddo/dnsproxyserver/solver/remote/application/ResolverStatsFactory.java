package com.mageddo.dnsproxyserver.solver.remote.application;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.remote.ResolverStats;
import com.mageddo.dnsproxyserver.solver.remote.mapper.ResolverMapper;

import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
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
        .filter(ResolverStats::isValidToUse)
        .map(ResolverStats::getResolver)
        .toList()
        ;
  }

  public ResolverStats find(Resolver resolver) {
    return ResolverMapper.toResolverStats(resolver,
        this.circuitBreakerService.findCircuitStatus(resolver.getAddress())
    );
  }
}
