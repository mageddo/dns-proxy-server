package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dnsproxyserver.server.dns.Messages;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

@Slf4j
@Singleton
public class SolverCachedRemote implements Solver {

  private final SolverRemote solverRemote;
  private final SolverCache solversCache;

  @Inject
  public SolverCachedRemote(
    SolverRemote solverRemote,
    @CacheName(name = Name.REMOTE) SolverCache cache
  ) {
    this.solverRemote = solverRemote;
    this.solversCache = cache;
  }

  @Override
  public Response handle(Message query) {
    final var res = this.solversCache.handleRes(query, query_ -> {
      log.debug("status=remoteHotLoading, query={}", Messages.simplePrint(query));
      return this.solverRemote.handle(query);
    });
    return Objects.mapOrNull(res, response -> response.withTTL(Duration.ofSeconds(20)));
  }
}
