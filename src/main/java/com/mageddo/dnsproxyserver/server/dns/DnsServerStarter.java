package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsserver.SimpleServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Getter
@Singleton
public class DnsServerStarter {

  private final SimpleServer server;

  @Inject
  public DnsServerStarter(SimpleServer server) {
    this.server = server;
  }

  public DnsServerStarter start() {
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
