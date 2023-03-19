package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.server.dns.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.utils.InetAddresses;
import dagger.Module;
import dagger.Provides;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import javax.enterprise.inject.Produces;
import java.time.Duration;
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
    return it -> {
      final var resolver = new SimpleResolver(InetAddresses.toSocketAddress(it.getRawIP(), it.getPortOrDef(53)));
      resolver.setTimeout(Duration.ofSeconds(10)); // default is 10 seconds
      return resolver;
    };
  }

}
