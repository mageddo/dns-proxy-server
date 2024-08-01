package com.mageddo.dnsproxyserver.solver.remote.mapper;

import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.ResolverStats;
import com.mageddo.dnsproxyserver.utils.InetAddresses;
import com.mageddo.net.IpAddr;
import org.apache.commons.compress.harmony.unpack200.bytecode.BCIRenumberedAttribute;

import java.net.InetSocketAddress;
import java.time.Duration;

public class ResolverMapper {

  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

  public static Resolver from(IpAddr addr) {
    return from(InetAddresses.toSocketAddress(addr.getRawIP(), addr.getPortOrDef(53)));
  }

  public static Resolver from(InetSocketAddress addr) {
    final var resolver = new SimpleResolver(addr);
    resolver.setTimeout(DEFAULT_TIMEOUT);
    return resolver;
  }

  public static ResolverStats toResolverStats(Resolver resolver, CircuitStatus status) {
    return ResolverStats
      .builder()
      .resolver(resolver)
      .circuitStatus(status)
      .build()
      ;
  }
}
