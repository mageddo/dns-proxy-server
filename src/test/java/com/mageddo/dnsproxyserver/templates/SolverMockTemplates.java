package com.mageddo.dnsproxyserver.templates;

import com.mageddo.dnsproxyserver.server.dns.solver.SolverMock;
import com.mageddo.net.IPI;
import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SolverMockTemplates {
  public static SolverMock whateverMock(String ... hostnames){
    final var mocks = Stream
      .of(hostnames)
      .map(it -> Pair.of(it, IPI.of("0.0.0.0")))
      .collect(Collectors.toList());
    return new SolverMock(mocks);
  }
}
