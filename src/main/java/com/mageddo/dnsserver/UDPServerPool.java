package com.mageddo.dnsserver;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.utils.Ips;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UDPServerPool {

  private final RequestHandler requestHandler;
  private List<UDPServer> servers = new ArrayList<>();

  public void start(int port) {
    this.servers = Collections.singletonList(
        new UDPServer(Ips.getAnyLocalAddress(port), this.requestHandler));
    this.servers.forEach(UDPServer::start);
    final var addresses = this.servers
        .stream()
        .map(UDPServer::getAddress)
        .map(SocketAddress::toString)
        .collect(Collectors.joining(", "));
    log.info("Starting UDP server, addresses={}", addresses);
  }

  public void stop() {
    this.servers
        .parallelStream()
        .forEach(UDPServer::stop)
    ;
  }
}
