package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.DpsIpDiscover;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Getter
@Singleton
public class ServerStarter {

  private final SimpleServer server;
  private final DpsIpDiscover dpsIpDiscover;

  @Inject
  public ServerStarter(SimpleServer server, DpsIpDiscover dpsIpDiscover) {
    this.server = server;
    this.dpsIpDiscover = dpsIpDiscover;
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
