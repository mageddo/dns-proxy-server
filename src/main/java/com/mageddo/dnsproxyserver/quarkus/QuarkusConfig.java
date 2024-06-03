package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.remote.mapper.ResolverMapper;
import com.mageddo.net.IpAddr;
import dagger.Module;
import dagger.Provides;

import javax.enterprise.inject.Produces;
import java.util.function.Function;

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
