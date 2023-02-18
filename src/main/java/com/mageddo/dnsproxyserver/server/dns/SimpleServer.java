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

  public void start(
      int port, Protocol protocol, List<Solver> solvers, InetAddress bindAddress
  ) {

    solvers.forEach(this.udpServer::bind);

    // fixme create tcp server

    this.udpServer.start(port, bindAddress);

  }

  public enum Protocol {
    UDP,
    TCP,
    BOTH
  }

}
