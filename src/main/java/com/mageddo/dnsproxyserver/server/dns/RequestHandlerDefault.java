package com.mageddo.dnsproxyserver.server.dns;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.CacheName;
import com.mageddo.dnsproxyserver.solver.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.SolverCache;
import com.mageddo.dnsproxyserver.solver.SolverProvider;
import com.mageddo.dnsserver.RequestHandler;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.xbill.DNS.Message;

import lombok.extern.slf4j.Slf4j;

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
    this.noEntriesRCode = Configs.getInstance()
        .getNoEntriesResponseCode();
  }

  @Override
  public Message handle(Message query, String kind) {
    final var queryStr = simplePrint(query);
    final var stopWatch = StopWatch.createStarted();
    log.debug("status=solveReq, kind={}, query={}", kind, queryStr);
    try {
      return this.solveCaching(query, kind, stopWatch, queryStr);
    } catch (Exception e) {
      log.warn(
          "status=solverFailed, totalTime={}, eClass={}, msg={}",
          stopWatch.getTime(), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return this.buildDefaultRes(query);
    }
  }

  Message solveCaching(Message query, String kind, StopWatch stopWatch, String queryStr) {
    final var res = Optional
        .ofNullable(this.cache.handle(query, this::solveFixingCacheTTL))
        .orElseGet(() -> this.buildDefaultRes(query));
    log.debug("status=solveRes, kind={}, time={}, res={}, req={}", kind, stopWatch.getTime(),
        simplePrint(res), queryStr
    );
    return res;
  }

  Response solveFixingCacheTTL(Message reqMsg) {
    return Objects.mapOrNull(this.solve(reqMsg), res -> res.withTTL(DEFAULT_GLOBAL_CACHE_DURATION));
  }

  Response solve(Message reqMsg) {
    final var timeSummary = new ArrayList<>();
    try {
      final var stopWatch = StopWatch.createStarted();
      final var solvers = this.solverProvider.getSolvers();
      for (final var solver : solvers) {
        final var triple = this.solveAndSummarizeHandlingError(reqMsg, solver, stopWatch);
        timeSummary.add(Pair.of(triple.getLeft(), triple.getMiddle()));
        if (triple.getRight() != null) {
          return triple.getRight();
        }
      }
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("status=solveSummary, summary={}", timeSummary);
      }
    }
    return null;
  }

  Triple<String, Long, Response> solveAndSummarizeHandlingError(Message reqMsg, Solver solver,
      StopWatch stopWatch) {
    final var solverName = solver.name();
    try {
      return this.solveAndSummarize(reqMsg, solver, stopWatch);
    } catch (Exception e) {
      log.warn(
          "status=solverFailed, currentSolverTime={}, totalTime={}, solver={}, query={}, "
              + "eClass={}, msg={}",
          stopWatch.getTime() - stopWatch.getSplitTime(), stopWatch.getTime(), solverName,
          simplePrint(reqMsg), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return null;
    }
  }

  Triple<String, Long, Response> solveAndSummarize(Message reqMsg, Solver solver,
      StopWatch stopWatch) {
    stopWatch.split();
    final var solverName = solver.name();
    final var reqStr = simplePrint(reqMsg);
    log.trace("status=trySolve, solver={}, req={}", solverName, reqStr);
    final var res = solver.handle(reqMsg);
    final var solverTime = stopWatch.getTime() - stopWatch.getSplitTime();
    if (res == null) {
      log.trace(
          "status=notSolved, currentSolverTime={}, totalTime={}, solver={}, req={}",
          solverTime, stopWatch.getTime(), solverName, reqStr
      );
      return Triple.of(solverName, solverTime, null);
    }
    log.debug(
        "status=solved, res={}, solver={}, answers={}, currentSolverTime={}, totalTime={}",
        simplePrint(res), solverName, res.countAnswers(), solverTime, stopWatch.getTime()
    );
    return Triple.of(solverName, solverTime, res);
  }

  public Message buildDefaultRes(Message reqMsg) {
    // if all failed and returned null, then return as can't find
    return Messages.withResponseCode(reqMsg, this.noEntriesRCode);
  }
}
