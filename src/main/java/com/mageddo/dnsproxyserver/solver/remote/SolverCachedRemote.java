package com.mageddo.dnsproxyserver.solver.remote;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.solver.NamedResponse;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.cache.CacheName;
import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.cache.SolverCache;

import com.mageddo.dnsproxyserver.solver.cache.Value;

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
    final var res = this.solversCache.handle(
        query,
        __ -> {
          if (log.isTraceEnabled()) {
            log.trace("status=hotload, q={}", Messages.simplePrint(query));
          }
          return NamedResponse.of(this.solverRemote.handle(query), this.name());
        }
    );
    return Objects.mapOrNull(res, Value::getSimpleResponse);
  }

  @Override
  public String name() {
    return NAME;
  }
}
