package com.mageddo.dnsproxyserver.solver;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.CacheName.Name;

import org.xbill.DNS.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class SolverCachedRemote implements Solver {

  public static final String NAME = "SolverCachedRemote";

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
    return this.solversCache.handleRes(query, query_ -> {
          log.debug("status=remoteHotLoading, query={}", Messages.simplePrint(query));
          return this.solverRemote.handle(query);
        }
    );
  }

  @Override
  public String name() {
    return NAME;
  }
}
