package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.IpAddr;
import com.mageddo.dnsproxyserver.server.dns.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.utils.InetAddresses;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import javax.enterprise.inject.Produces;

import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class QuarkusConfig {

  @Produces
  public RemoteResolvers remoteResolvers(Function<IpAddr, Resolver> resolverProvider) {
    final var servers = Configs
      .getInstance()
      .getRemoteDnsServers();
    return RemoteResolvers.of(servers, resolverProvider);
  }

  @Produces
  public Function<IpAddr, Resolver> getResolverProvider() {
    return it -> (Resolver) new SimpleResolver(InetAddresses.toSocketAddress(it.getRawIP(), it.getPortOrDef(53)));
  }

  public static void setup(Config config) {
    System.setProperty("quarkus.http.port", String.valueOf(config.getWebServerPort()));
    System.setProperty("quarkus.log.level", config.getLogLevel().name());
    final var logFile = Configs.parseLogFile(config.getLogFile());
    if (logFile == null) {
      System.setProperty("quarkus.log.console.enable", "false");
      System.setProperty("quarkus.log.file.enable", "false");
    } else if (!equalsIgnoreCase(logFile, "console")) {
      System.setProperty("quarkus.log.console.enable", "false");
      System.setProperty("quarkus.log.file.enable", "true");
      System.setProperty("quarkus.log.file.path", config.getLogFile());
    }
  }
}
