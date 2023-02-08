package com.mageddo.dnsproxyserver.server.dns.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Solvers {
  public static List<Solver> sorted(Collection<Solver> source) {
    final var solvers = new ArrayList<>(source);
    solvers.sort(Comparator.comparing(Solver::priority));
    return solvers;
  }
}
