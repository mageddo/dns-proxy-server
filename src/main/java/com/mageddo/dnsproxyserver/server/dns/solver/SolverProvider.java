package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.utils.Priorities;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class SolverProvider {

  private static final String[] solversOrder = {
    "SolverCached",
    "SolverSystem",
    "SolverDocker",
    SolverLocalDB.NAME,
    SolverCachedRemote.NAME
  };

  private final List<Solver> solvers;

  @Inject
  public SolverProvider(Instance<Solver> solvers) {
    final var config = Configs.getInstance();
    this.solvers = solvers
      .stream()
      .sorted(Priorities.comparator(Solver::name, solversOrder))
      .filter(it -> it.is(SolverCachedRemote.NAME) && !config.isNoRemoteServers())
      .toList()
    ;
  }

  public List<Solver> getSolvers() {
    return this.solvers;
  }

  public List<Solver> getSolversExcluding(final Class<?> clazz) {
    return this.solvers
      .stream()
      .filter(it -> it.getClass() != clazz)
      .collect(Collectors.toList())
      ;
  }
}
