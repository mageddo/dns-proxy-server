package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Getter
@Singleton
public class ServerStarter {

  private final List<Solver> solvers;
  private final SimpleServer server;

  @Inject
  public ServerStarter(Instance<Solver> solvers, SimpleServer server) {
    this.solvers = new SolverProvider(solvers).getSolvers();
    this.server = server;
  }

  public ServerStarter start() {
    final var port = Configs.getInstance().getDnsServerPort();
    this.server.start(
      port,
      Config.findDnsServerProtocol(),
      this.solvers,
      null
    );
    log.info("status=startingDnsServer, port={}", port);
    return this;
  }

}
