package com.mageddo.dnsproxyserver.server.dns;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.commons.lang.Objects;
import com.mageddo.dns.utils.Messages;
import com.mageddo.dnsproxyserver.config.application.Configs;
import com.mageddo.dnsproxyserver.solver.NamedResponse;
import com.mageddo.dnsproxyserver.solver.Response;
import com.mageddo.dnsproxyserver.solver.Solver;
import com.mageddo.dnsproxyserver.solver.SolverProvider;
import com.mageddo.dnsproxyserver.solver.cache.CacheName;
import com.mageddo.dnsproxyserver.solver.cache.CacheName.Name;
import com.mageddo.dnsproxyserver.solver.cache.SolverCache;
import com.mageddo.dnsserver.RequestHandler;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.xbill.DNS.Message;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
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
    final var stopWatch = StopWatch.createStarted();
    if (log.isTraceEnabled()) {
      final var queryStr = simplePrint(query);
      log.trace("status=solving, kind={}, query={}", kind, queryStr);
    }
    try {
      return this.solveCaching(query, kind, stopWatch);
    } catch (Exception e) {
      log.warn(
          "status=solverFailed, totalTime={}, eClass={}, msg={}",
          stopWatch.getTime(), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return this.buildDefaultRes(query);
    }
  }

  Message solveCaching(Message query, String kind, StopWatch stopWatch) {
    final var value = this.cache.handle(query, this::solveWithFixedCacheTTL);
    if (value == null) {
      final var msg = this.buildDefaultRes(query);
      log.debug(
          "status=defaultAnswer, kind={}, totalTime={}, res={}",
          kind, stopWatch.getTime(), simplePrint(msg)
      );
      return msg;
    }
    log.debug(
        "status=solved, entrypoint={}, hotload={}, solver={}, totalTime={}, ttl={}, answers={}, res={}",
        kind, value.isHotload(),
        value.getSolver(),
        stopWatch.getTime(),
        value.getTTLAsSeconds(),
        value.countAnswers(),
        simplePrint(value.getMessage())
    );
    return value.getMessage();
  }

  NamedResponse solveWithFixedCacheTTL(Message req) {
    return Objects.mapOrNull(
        this.solve(req),
        res -> res.withTTL(DEFAULT_GLOBAL_CACHE_DURATION)
    );
  }

  NamedResponse solve(Message req) {
    final var timeSummary = new ArrayList<>();
    try {
      final var stopWatch = StopWatch.createStarted();
      final var solvers = this.getSolvers();
      for (final var solver : solvers) {
        final var res = this.solveTracking(req, solver, stopWatch);
        timeSummary.add(Pair.of(res.getSolverName(), res.getSolverTime()));
        if (res.hasResponse()) {
          return NamedResponse.of(res.getResponse(), solver.name());
        }
      }
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("req={} timesSummary={}", Messages.simplePrint(req), timeSummary);
      }
    }
    return null;
  }

  TrackedResponse solveTracking(
      Message req, Solver solver, StopWatch stopWatch
  ) {
    try {
      return this.solveTracking0(req, solver, stopWatch);
    } catch (Exception e) {
      final var solverName = solver.name();
      final var solverTime = stopWatch.getTime() - stopWatch.getSplitTime();
      log.warn(
          "status=failed, solverTime={}, totalTime={}, solver={}, query={}, "
              + "eClass={}, msg={}",
          solverTime, stopWatch.getTime(), solverName,
          simplePrint(req), ClassUtils.getSimpleName(e), e.getMessage(), e
      );
      return TrackedResponse.builder()
          .solverName(solverName)
          .solverTime(solverTime)
          .build();
    }
  }

  TrackedResponse solveTracking0(
      Message reqMsg, Solver solver, StopWatch stopWatch
  ) {
    stopWatch.split();
    final var solverName = solver.name();
    final var reqStr = simplePrint(reqMsg);
    if (log.isTraceEnabled()) {
      log.trace("status=trySolve, solver={}, req={}", solverName, reqStr);
    }
    final var res = solver.handle(reqMsg);
    final var solverTime = stopWatch.getTime() - stopWatch.getSplitTime();
    if (res == null) {
      log.trace(
          "status=notSolved, currentSolverTime={}, totalTime={}, solver={}, req={}",
          solverTime, stopWatch.getTime(), solverName, reqStr
      );
      return TrackedResponse.builder()
          .solverName(solverName)
          .solverTime(solverTime)
          .build();
    }
    if (log.isTraceEnabled()) {
      log.trace(
          "status=solved, res={}, solver={}, answers={}, currentSolverTime={}, totalTime={}",
          simplePrint(res), solverName, res.countAnswers(), solverTime, stopWatch.getTime()
      );
    }
    return TrackedResponse.builder()
        .solverName(solverName)
        .solverTime(solverTime)
        .response(res)
        .build();
  }

  List<Solver> getSolvers() {
    return this.solverProvider.getSolvers();
  }

  Message buildDefaultRes(Message reqMsg) {
    // if all failed and returned null, then return as can't find
    return Messages.withResponseCode(reqMsg, this.noEntriesRCode);
  }

  @Value
  @Builder
  static class TrackedResponse {

    @NonNull
    Long solverTime;

    @NonNull
    String solverName;

    Response response;

    public boolean hasResponse() {
      return this.response != null;
    }
  }
}
