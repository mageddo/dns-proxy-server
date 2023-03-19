package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.server.dns.Messages;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

@Slf4j
@Singleton
public class SolverCachedRemote implements Solver {

  private final SolverRemote solverRemote;
  private final SolversCache solversCache;

  @Inject
  public SolverCachedRemote(SolverRemote solverRemote) {
    this.solverRemote = solverRemote;
    this.solversCache = new SolversCache();
  }

  @Override
  public Response handle(Message query) {
    final var res = this.solversCache.handleRes(query, query_ -> {
      log.debug("status=remoteHotLoading, query={}", Messages.simplePrint(query));
      return this.solverRemote.handle(query);
    });
    return res
      .toBuilder()
      .ttl(Duration.ofSeconds(20))
      .build()
      ;
  }
}
