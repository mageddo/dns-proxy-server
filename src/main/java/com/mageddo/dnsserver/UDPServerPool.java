package com.mageddo.dnsserver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.Collections;
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
    final var addresses = List.of(
        Ips.getAnyLocalIpv6Address(port),
        Ips.getAnyLocalAddress(port)
    );
    this.servers = Collections.map(
        addresses, address -> new UDPServer(address, this.requestHandler)
    );
    this.servers.forEach(UDPServer::start);
    log.info("Starting UDP server, addresses={}", this.toString(addresses));
  }

  private String toString(List<InetSocketAddress> addresses) {
    return addresses.stream()
        .map(SocketAddress::toString)
        .collect(Collectors.joining(", "));
  }

  public void stop() {
    this.servers
        .parallelStream()
        .forEach(UDPServer::stop)
    ;
  }
}
