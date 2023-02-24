package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.net.Networks;
import com.mageddo.dnsproxyserver.utils.Ips;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UDPServerPool {

  private final RequestHandler requestHandler;
  private List<UDPServer> servers = new ArrayList<>();

  public void start(int port) {
    this.servers = Networks
      .findMachineIps()
      .stream()
      .map(it -> new UDPServer(Ips.toSocketAddress(it.raw(), port), this.requestHandler))
      .peek(UDPServer::start)
      .toList();
    final var addresses = this.servers
      .stream()
      .map(UDPServer::getAddress)
      .map(SocketAddress::toString)
      .collect(Collectors.joining(", "));
    log.info("Starting UDP server, addresses={}", addresses);
  }
}
