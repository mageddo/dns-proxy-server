package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.utils.Priorities;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class SolverProvider {

  private static final Map<String, Integer> priorities = Priorities.build(
    "SolverCached", "SolverSystem", "SolverDocker", "SolverLocalDB", "SolverCachedRemote"
  );

  private final List<Solver> solvers;

  @Inject
  public SolverProvider(Instance<Solver> solvers) {
    this.solvers = sorted(solvers);
  }

  public List<Solver> getSolversExcluding(final Class<?> clazz) {
    return this.solvers
        .stream()
        .filter(it -> it.getClass() != clazz)
        .collect(Collectors.toList())
        ;
  }

  public List<Solver> getSolvers() {
    return this.solvers;
  }

  static List<Solver> sorted(Instance<Solver> solvers) {
    return sorted(solvers.stream().toList());
  }

  public static List<Solver> sorted(Collection<Solver> source) {
    final var solvers = new ArrayList<>(source);
    solvers.sort(comparator());
    return solvers;
  }

  public static Comparator<Solver> comparator() {
    return Comparator.comparing(it -> Priorities.compare(priorities, it.name()));
  }
}
