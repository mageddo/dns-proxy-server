package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.CacheName;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import com.mageddo.dnsproxyserver.server.dns.solver.Response;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverCache;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverProvider;
import com.mageddo.dnsproxyserver.utils.Classes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static com.mageddo.dnsproxyserver.server.dns.Messages.simplePrint;

@Slf4j
@Singleton
public class RequestHandlerDefault implements RequestHandler {

  private final SolverProvider solverProvider;
  private final SolverCache cache;

  @Inject
  public RequestHandlerDefault(
    SolverProvider solverProvider,
    @CacheName(name = Name.GLOBAL) SolverCache cache
  ) {
    this.solverProvider = solverProvider;
    this.cache = cache;
  }

  @Override
  public Message handle(Message query, String kind) {
    return this.solve(query, kind);
  }

  Message solve(Message query, String kind) {

    final var stopWatch = StopWatch.createStarted();
    try {
      final var r = Optional
        .ofNullable(this.cache.handle(query, this::solve0))
        .orElseGet(() -> buildDefaultRes(query));
      log.debug("status=solved, kind={}, time={}, res={}", kind, stopWatch.getTime(), simplePrint(r));
      return r;
    } catch (Exception e) {
      log.warn(
        "status=solverFailed, totalTime={}, eClass={}, msg={}",
        stopWatch.getTime(), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return buildDefaultRes(query);
    }
  }

  Response solve0(Message reqMsg) {
    final var stopWatch = StopWatch.createStarted();
    final var solvers = this.solverProvider.getSolvers();
    for (final var solver : solvers) {
      stopWatch.split();
      final var solverName = Classes.findSimpleName(solver);
      try {
        final var reqStr = simplePrint(reqMsg);
        log.trace("status=trySolve, solver={}, req={}", solverName, reqStr);
        final var res = solver.handle(reqMsg);
        if (res == null) {
          log.trace(
            "status=notSolved, currentSolverTime={}, totalTime={}, solver={}, req={}",
            stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName, reqStr
          );
          continue;
        }
        log.debug(
          "status=solved, currentSolverTime={}, totalTime={}, solver={}, req={}, res={}",
          stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName, reqStr, simplePrint(res)
        );
        return res;
      } catch (Exception e) {
        log.warn(
          "status=solverFailed, currentSolverTime={}, totalTime={}, solver={}, eClass={}, msg={}",
          stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName,
          ClassUtils.getSimpleName(e), e.getMessage(), e
        );
      }
    }
    return null;
  }

  public static Message buildDefaultRes(Message reqMsg) {
    return Messages.nxDomain(reqMsg); // if all failed and returned null, then return as can't find
  }
}
