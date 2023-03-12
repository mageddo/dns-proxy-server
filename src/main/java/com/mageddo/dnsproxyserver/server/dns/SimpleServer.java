package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SimpleServer {

  private final UDPServerPool udpServerPool;
  private final TCPServer tcpServer;
  private final RequestHandlerDefault requestHandler;

  public void start(int port, Protocol protocol, List<Solver> solvers) {

    solvers.forEach(this.requestHandler::bind);
    this.start0(port, protocol);

  }

  void start0(int port, Protocol protocol) {
    final var tcpHandler = new DnsQueryTCPHandler(this.requestHandler);
    switch (protocol) {
      case UDP -> this.udpServerPool.start(port);
      case TCP -> {
        this.tcpServer.start(port, null, tcpHandler);
      }
      default -> {
        this.udpServerPool.start(port);
        this.tcpServer.start(port, null, tcpHandler);
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
    BOTH
  }

}
