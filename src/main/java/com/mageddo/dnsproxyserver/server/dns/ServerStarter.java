package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.docker.application.DpsContainerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Getter
@Singleton
public class ServerStarter {

  private final SimpleServer server;
  private final DpsContainerService dpsContainerService;

  @Inject
  public ServerStarter(SimpleServer server, DpsContainerService dpsContainerService) {
    this.server = server;
    this.dpsContainerService = dpsContainerService;
  }

  public ServerStarter start() {
    final var config = Configs.getInstance();
    final var port = config.getDnsServerPort();
    this.server.start(
      port,
      config.getServerProtocol()
    );
    log.info("status=startingDnsServer, protocol={}, port={}", config.getServerProtocol(), port);
    return this;
  }

  public void stop() {
    this.server.stop();
  }
}
