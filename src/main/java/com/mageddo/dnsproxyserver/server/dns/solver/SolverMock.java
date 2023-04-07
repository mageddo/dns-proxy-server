package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.net.IP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.xbill.DNS.Message;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class SolverMock implements Solver {

  public static final String NAME = "SolverMock";
  private final List<Pair<String, IP>> mocks;
  private final String name;

  public SolverMock(Pair<String, IP>... mocks) {
    this(NAME, mocks);
  }

  public SolverMock(String name, Pair<String, IP>... mocks) {
    this(name, Stream.of(mocks).toList());
  }

  public SolverMock(List<Pair<String, IP>> mocks) {
    this(NAME, mocks);
  }

  public SolverMock(String name, List<Pair<String, IP>> mocks) {
    this.name = name;
    this.mocks = mocks;
  }

  @Override
  public Response handle(Message query) {
    final var hostname = Messages.findQuestionHostname(query);
    for (final var entry : this.mocks) {
      if (entry.getKey().equalsIgnoreCase(hostname.getValue())) {
        return Response.of(Messages.aAnswer(query, entry.getValue().toText()));
      }
    }
    return null;
  }

  @Override
  public String name() {
    return this.name;
  }
}
