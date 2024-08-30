package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.circuitbreaker.failsafe.CircuitStatusRefresh;
import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategy;
import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.application.FailsafeCircuitBreakerFactory;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateFailsafe;
import com.mageddo.dnsproxyserver.solver.remote.mapper.CircuitBreakerStateMapper;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFactory {

  private final Map<InetSocketAddress, CircuitBreaker<Result>> circuitBreakerMap = new ConcurrentHashMap<>();
  private final ConfigService configService;
  private final CircuitBreakerPingCheckerService circuitBreakerCheckerService;
  private final FailsafeCircuitBreakerFactory failsafeCircuitBreakerFactory;

  public CircuitBreakerDelegate findCircuitBreaker(InetSocketAddress resolverAddress) {
    return new CircuitBreakerDelegateFailsafe(this.createOrGetCircuitBreaker(resolverAddress));
  }

  public Result check(InetSocketAddress remoteAddress, Supplier<Result> sup) {
    final var circuitBreaker = this.createOrGetCircuitBreaker(remoteAddress);
    return Failsafe
      .with(circuitBreaker)
      .get((ctx) -> sup.get());
  }

  CircuitBreaker<Result> createOrGetCircuitBreaker(InetSocketAddress address) {
    final var config = this.findCircuitBreakerConfig();

    return this.circuitBreakerMap.computeIfAbsent(
      address,
      addr -> this.failsafeCircuitBreakerFactory.build(addr, config)
    );
  }


  StaticThresholdCircuitBreakerStrategy findCircuitBreakerConfig() {
    // fixme #533 this could not work every time, check it
    return (StaticThresholdCircuitBreakerStrategy) this.configService.findCurrentConfig()
      .getSolverRemote()
      .getCircuitBreaker();
  }

  public Pair<Integer, Integer> checkCreatedCircuits() {
    final var stopWatch = StopWatch.createStarted();
    log.debug("status=checkingCreatedCircuits, circuits={}", this.circuitBreakerMap.size());
    int successes = 0, errors = 0;
    for (final var entry : this.circuitBreakerMap.entrySet()) {
      if (this.circuitBreakerSafeCheck(entry)) {
        successes++;
      } else {
        errors++;
      }
    }
    log.debug(
      "status=checkEnded, successes={}, errors={}, circuits={}, timeElapsed={}",
      successes, errors, this.circuitBreakerMap.size(), stopWatch.getTime()
    );
    return Pair.of(successes, errors);
  }

  boolean circuitBreakerSafeCheck(Map.Entry<InetSocketAddress, CircuitBreaker<Result>> entry) {
    return this.circuitBreakerCheckerService.safeCheck(entry.getKey(), entry.getValue());
  }

  public void reset() {
    this.circuitBreakerMap.clear();
  }

  public List<Stats> stats() {
    return this.circuitBreakerMap.keySet()
      .stream()
      .map(this::toStats)
      .toList();
  }

  public CircuitStatus findStatus(InetSocketAddress remoteAddress) {
    final var circuit = this.circuitBreakerMap.get(remoteAddress);
    CircuitStatusRefresh.refresh(circuit);
    return CircuitBreakerStateMapper.fromFailSafeCircuitBreaker(circuit);
  }

  private Stats toStats(InetSocketAddress remoteAddr) {
    final var circuitBreaker = this.circuitBreakerMap.get(remoteAddr);
    final var state = circuitBreaker.getState().name();
    return Stats.of(remoteAddr.toString(), state);
  }

  @Value
  public static class Stats {

    private String remoteServerAddress;
    private String state;

    public static Stats of(String remoteServerAddress, String state) {
      return new Stats(remoteServerAddress, state);
    }
  }

}
