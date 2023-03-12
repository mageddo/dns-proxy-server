package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.di.Context;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class SolverProvider {

  private List<Solver> solvers;
  private final AtomicBoolean count = new AtomicBoolean();

  @Inject
  public SolverProvider() {
  }

  public SolverProvider(Instance<Solver> solvers) {
    this.solvers = sorted(solvers);
  }

  public List<Solver> getSolversExcludingLocalDB() {
    this.lazyLoad();
    return this.solvers
        .stream()
        .filter(it -> it.getClass() != SolverLocalDB.class)
        .collect(Collectors.toList())
        ;
  }

  public List<Solver> getSolvers() {
    this.lazyLoad();
    return this.solvers;
  }

  void lazyLoad() {
    if (this.count.compareAndSet(false, true)) {
      this.solvers = sorted(Context
          .create()
          .solvers()
      );
      log.debug("status=instantesSet, size={}", this.solvers.size());
    }
  }

  static List<Solver> sorted(Instance<Solver> solvers) {
    return Solvers.sorted(solvers.stream().toList());
  }
}
