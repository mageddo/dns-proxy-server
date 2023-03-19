package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.utils.Priorities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Solvers {

  private static final Map<String, Integer> priorities = Priorities.build(
    "SolverCached", "SolverSystem", "SolverDocker", "SolverLocalDB", "SolverCachedRemote"
  );

  public static List<Solver> sorted(Collection<Solver> source) {
    final var solvers = new ArrayList<>(source);
    solvers.sort(Solvers.comparator());
    return solvers;
  }

  public static Comparator<Solver> comparator() {
    return Comparator.comparing(it -> Priorities.compare(priorities, it.name()));
  }

}
