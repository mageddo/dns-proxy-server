package com.mageddo.dnsserver;

import java.net.InetAddress;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.server.dns.RequestHandlerDefault;
import com.mageddo.dnsproxyserver.utils.Ips;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SimpleServer {

  private final UDPServerPool udpServerPool;
  private final TCPServer tcpServer;
  private final RequestHandlerDefault requestHandler;

  public void start(int port, Protocol protocol) {
    this.start(protocol, Ips.getAnyLocalAddress(), port);
  }

  public void start(Protocol protocol, InetAddress addr, int port) {
    this.start0(protocol, addr, port);
  }

  void start0(Protocol protocol, InetAddress addr, int port) {
    final var tcpHandler = new DnsQueryTCPHandler(this.requestHandler);
    switch (protocol) {
      case UDP -> this.udpServerPool.start(addr, port);
      case TCP -> {
        this.tcpServer.start(port, addr, tcpHandler);
      }
      default -> {
        this.udpServerPool.start(addr, port);
        this.tcpServer.start(port, addr, tcpHandler);
      }
    }
  }

  public void stop() {
    this.udpServerPool.stop();
    this.tcpServer.stop();
  }

  public enum Protocol {
    UDP,
    TCP,
    UDP_TCP
  }

}
