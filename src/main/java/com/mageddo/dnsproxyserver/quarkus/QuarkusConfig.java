package com.mageddo.dnsproxyserver.quarkus;

import java.util.function.Function;

import javax.enterprise.inject.Produces;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.remote.mapper.ResolverMapper;
import com.mageddo.net.IpAddr;

import dagger.Module;
import dagger.Provides;

@Module
public class QuarkusConfig {

  @Produces
  @Provides
  public RemoteResolvers remoteResolvers(Function<IpAddr, Resolver> resolverProvider) {
    final var servers = Configs
        .getInstance()
        .getRemoteDnsServers();
    return RemoteResolvers.of(servers, resolverProvider);
  }

  @Produces
  @Provides
  public Function<IpAddr, Resolver> getResolverProvider() {
    return ResolverMapper::from;
  }

}
