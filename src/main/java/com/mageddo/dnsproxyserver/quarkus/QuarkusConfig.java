package com.mageddo.dnsproxyserver.quarkus;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.RemoteResolvers;

import javax.enterprise.inject.Produces;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class QuarkusConfig {

  @Produces
  public RemoteResolvers remoteResolvers() {
    final var servers = Configs
      .getInstance()
      .getRemoteDnsServers();
    return RemoteResolvers.of(servers);
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
