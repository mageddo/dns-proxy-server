package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.util.List;

@Slf4j
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public class SimpleServer {

  private final UDPServer udpServer;
  private final TCPServer tcpServer;
  private final RequestHandler requestHandler;

  public void start(
    int port, Protocol protocol, List<Solver> solvers, InetAddress bindAddress
  ) {

    solvers.forEach(this.requestHandler::bind);
    this.start0(port, protocol, bindAddress);

  }

  void start0(int port, Protocol protocol, InetAddress bindAddress) {
    switch (protocol) {
      case UDP -> this.udpServer.start(port, bindAddress);
      case TCP -> this.tcpServer.start(port, bindAddress);
      default -> {
        this.udpServer.start(port, bindAddress);
        this.tcpServer.start(port, bindAddress);
      }
    }
  }

  public enum Protocol {
    UDP,
    TCP,
    BOTH
  }

}
