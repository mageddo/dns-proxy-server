package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dnsproxyserver.solver.remote.Request;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.application.CircuitBreakerService;
import com.mageddo.dnsproxyserver.solver.remote.application.RemoteResultSupplier;
import com.mageddo.dnsproxyserver.solver.remote.application.ResolverStatsFactory;
import com.mageddo.dnsproxyserver.solver.remote.application.ResultSupplier;
import com.mageddo.net.NetExecutorWatchdog;
import com.mageddo.utils.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.xbill.DNS.Message;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SolverRemote implements Solver, AutoCloseable {

  private final CircuitBreakerService circuitBreakerService;
  private final ResolverStatsFactory resolverStatsFactory;
  private final NetExecutorWatchdog netWatchdog = new NetExecutorWatchdog();
  private final ExecutorService executor = Executors.newThreadExecutor();

  @Override
  public Response handle(Message query) {
    final var stopWatch = StopWatch.createStarted();
    final var result = this.queryResultFromAvailableResolvers(query, stopWatch);
    log.debug(
      "status=finally, time={}, success={}, error={}",
      stopWatch.getTime(), result.hasSuccessMessage(), result.hasErrorMessage()
    );
    return Stream
      .of(result.getSuccessResponse(), result.getErrorResponse())
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  Result queryResultFromAvailableResolvers(Message query, StopWatch stopWatch) {
    final var lastErrorMsg = new AtomicReference<Message>();
    final var resolvers = this.findResolversToUse();
    for (int i = 0; i < resolvers.size(); i++) {

      final var resolver = resolvers.get(i);
      final var request = this.buildRequest(query, i, stopWatch, resolver);

      final var result = this.safeQueryResult(request);

      if (result.hasSuccessMessage()) {
        return result;
      } else if (result.hasErrorMessage()) {
        lastErrorMsg.set(result.getErrorMessage());
      }

    }
    return Result.fromErrorMessage(lastErrorMsg.get());
  }

  List<Resolver> findResolversToUse() {
    return this.resolverStatsFactory.findResolversWithNonOpenCircuit();
  }

  Request buildRequest(Message query, int resolverIndex, StopWatch stopWatch, Resolver resolver) {
    return Request
      .builder()
      .resolverIndex(resolverIndex)
      .query(query)
      .stopWatch(stopWatch)
      .resolver(resolver)
      .build();
  }

  Result safeQueryResult(Request req) {
    req.splitStopWatch();
    return this.queryUsingCircuitBreaker(new RemoteResultSupplier(req, this.executor, this.netWatchdog));
  }

  Result queryUsingCircuitBreaker(ResultSupplier sup) {
    return this.circuitBreakerService.safeHandle(sup);
  }

  @Override
  public void close() throws Exception {
    this.netWatchdog.close();
  }

}
