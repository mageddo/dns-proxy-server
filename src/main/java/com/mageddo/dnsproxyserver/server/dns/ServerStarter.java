package com.mageddo.dnsproxyserver.server.dns;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsserver.SimpleServer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Singleton
public class ServerStarter {

  private final SimpleServer server;

  @Inject
  public ServerStarter(SimpleServer server) {
    this.server = server;
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
