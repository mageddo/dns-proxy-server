package com.mageddo.dnsserver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.Collections;
import com.mageddo.dnsproxyserver.utils.InetAddresses;
import com.mageddo.dnsproxyserver.utils.Ips;
import com.mageddo.net.IP;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UDPServerPool {

  private final RequestHandler requestHandler;
  private List<UDPServer> servers = new ArrayList<>();

  public void start(int port) {
    final var addresses = this.buildAddressesToBind(port);
    this.servers = Collections.map(
        addresses,
        address -> new UDPServer(address, this.requestHandler)
    );
    this.servers.forEach(UDPServer::start);
    log.info("Starting UDP server, addresses={}", this.toString(addresses));
  }

  private List<InetSocketAddress> buildAddressesToBind(int port) {
    final var bindIp = Ips.from(Ips.getAnyLocalIpv6Address());
    return this.buildAddressesToBind(bindIp, port);
  }

  private List<InetSocketAddress> buildAddressesToBind(IP ip, int port) {
    return Collections.map(
        Addresses.findBindAddresses(ip),
        it -> InetAddresses.toSocketAddress(it, port)
    );
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
