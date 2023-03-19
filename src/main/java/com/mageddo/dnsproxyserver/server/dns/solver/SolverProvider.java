package com.mageddo.dnsproxyserver.server.dns.solver;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class SolverProvider {

  private List<Solver> solvers;

  @Inject
  public SolverProvider(Instance<Solver> solvers) {
    this.solvers = sorted(solvers);
  }

  public List<Solver> getSolversExcludingLocalDB() {
    return this.solvers
        .stream()
        .filter(it -> it.getClass() != SolverLocalDB.class)
        .collect(Collectors.toList())
        ;
  }

  public List<Solver> getSolvers() {
    return this.solvers;
  }

  static List<Solver> sorted(Instance<Solver> solvers) {
    return Solvers.sorted(solvers.stream().toList());
  }
}
