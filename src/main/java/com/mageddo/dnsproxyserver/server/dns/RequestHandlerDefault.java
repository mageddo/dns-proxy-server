package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName;
import com.mageddo.dnsproxyserver.server.dns.solver.CacheName.Name;
import com.mageddo.dnsproxyserver.server.dns.solver.Response;
import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverCache;
import com.mageddo.dnsproxyserver.server.dns.solver.SolverProvider;
import com.mageddo.dnsserver.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mageddo.dns.utils.Messages.simplePrint;

@Slf4j
@Singleton
public class RequestHandlerDefault implements RequestHandler {

  public static final Duration DEFAULT_GLOBAL_CACHE_DURATION = Duration.ofSeconds(20);

  private final SolverProvider solverProvider;
  private final SolverCache cache;
  private final int noEntriesRCode;

  @Inject
  public RequestHandlerDefault(
    SolverProvider solverProvider,
    @CacheName(name = Name.GLOBAL) SolverCache cache
  ) {
    this.solverProvider = solverProvider;
    this.cache = cache;
    this.noEntriesRCode = Configs.getInstance().getNoEntriesResponseCode();
  }

  @Override
  public Message handle(Message query, String kind) {
    final var queryStr = simplePrint(query);
    final var stopWatch = StopWatch.createStarted();
    log.debug("status=solveReq, kind={}, query={}", kind, queryStr);
    try {
      final var res = Optional
        .ofNullable(this.cache.handle(query, this::solveCaching))
        .orElseGet(() -> buildDefaultRes(query));
      log.debug("status=solveRes, kind={}, time={}, res={}, req={}", kind, stopWatch.getTime(), simplePrint(res), queryStr);
      return res;
    } catch (Exception e) {
      log.warn(
        "status=solverFailed, totalTime={}, eClass={}, msg={}",
        stopWatch.getTime(), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return buildDefaultRes(query);
    }
  }

  Response solveCaching(Message reqMsg) {
    return Objects.mapOrNull(this.solve(reqMsg), res -> res.withTTL(DEFAULT_GLOBAL_CACHE_DURATION));
  }

  Response solve(Message reqMsg) {
    final var stopWatch = StopWatch.createStarted();
    final var solvers = this.solverProvider.getSolvers();
    final var timeSummary = new ArrayList<>();
    try {
      for (final var solver : solvers) {
        final var res = this.solve(timeSummary, reqMsg, solver, stopWatch);
        if (res != null) {
          return res;
        }
      }
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("status=solveSummary, summary={}", timeSummary);
      }
    }
    return null;
  }

  Response solve(List<Object> timeSummary, Message reqMsg, Solver solver, StopWatch stopWatch) {
    stopWatch.split();
    final var solverName = solver.name();
    try {
      final var reqStr = simplePrint(reqMsg);
      log.trace("status=trySolve, solver={}, req={}", solverName, reqStr);
      final var res = solver.handle(reqMsg);
      final var solverTime = stopWatch.getTime() - stopWatch.getSplitTime();
      if (log.isDebugEnabled()) {
        timeSummary.add(Pair.of(solverName, solverTime));
      }
      if (res == null) {
        log.trace(
          "status=notSolved, currentSolverTime={}, totalTime={}, solver={}, req={}",
          solverTime, stopWatch.getTime(), solverName, reqStr
        );
        return null;
      }
      log.debug(
        "status=solved, currentSolverTime={}, totalTime={}, solver={}, req={}, res={}",
        solverTime, stopWatch.getTime(), solverName, reqStr, simplePrint(res)
      );
      return res;
    } catch (Exception e) {
      log.warn(
        "status=solverFailed, currentSolverTime={}, totalTime={}, solver={}, query={}, eClass={}, msg={}",
        stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName,
        simplePrint(reqMsg), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return null;
    }
  }

  public Message buildDefaultRes(Message reqMsg) {
    // if all failed and returned null, then return as can't find
    return Messages.withResponseCode(reqMsg, this.noEntriesRCode);
  }
}
