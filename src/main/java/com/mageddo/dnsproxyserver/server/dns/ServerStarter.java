package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.quarkus.Instances;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.Solvers;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Getter
@Singleton
@Accessors(fluent = true)
public class ServerStarter {
  private final List<Solver> solvers;
  private final SimpleServer server;

  @Inject
  public ServerStarter(Instance<Solver> solvers, SimpleServer server) {
    this.solvers = Solvers.sorted(Instances.toSet(solvers));
    this.server = server;
  }

  public ServerStarter start(){
    this.server.start(
        Configs.findDnsServerPort(),
        Configs.findDnsServerProtocol(),
        this.solvers,
        null
    );
    log.info("status=startingDnsServer");
    return this;
  }
}