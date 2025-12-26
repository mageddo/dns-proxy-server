package com.mageddo.dnsproxyserver.server.dns;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.Config.Server;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.dnsserver.SimpleServer;
import com.mageddo.dnsserver.doh.DoHServer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ServerStarter {

  private final SimpleServer server;
  private final DoHServer doHServer;

  public ServerStarter start() {
    final var server = Configs.getInstance()
        .getServer();
    final var address = Ips.toAddress(server.getHost());

    final var dns = server.getDns();
    this.server.start(dns.getProtocol(), address, dns.getPort());

    this.startDohWhenNeedled(server.getDoh(), address);

    log.info("status=startingDnsServer, protocol={}, port={}", dns.getProtocol(), dns.getPort());
    return this;
  }

  void startDohWhenNeedled(Server.DoH doh, InetAddress address) {
    if (doh != null && doh.isActive()) {
      this.doHServer.start(new InetSocketAddress(address, doh.getPort()));
    }
  }

  public void stop() {
    this.server.stop();
    this.doHServer.close();
  }
}
