package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.dnsconfigurator.DpsIpDiscover;
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
  private final DpsIpDiscover dpsIpDiscover;

  @Inject
  public ServerStarter(Instance<Solver> solvers, SimpleServer server, DpsIpDiscover dpsIpDiscover) {
    this.solvers = new SolverProvider(solvers).getSolvers(); // todo nao precisaria criar na mao, usando o lazy
    this.server = server;
    this.dpsIpDiscover = dpsIpDiscover;
  }

  public ServerStarter start() {
    final var config = Configs.getInstance();
    final var port = config.getDnsServerPort();
    this.server.start(
      port,
      config.getServerProtocol(),
      this.solvers
    );
    log.debug("status=startingDnsServer, protocol={}, port={}", config.getServerProtocol(), port);
    return this;
  }

  public void stop() {
    this.server.stop();
  }
}
