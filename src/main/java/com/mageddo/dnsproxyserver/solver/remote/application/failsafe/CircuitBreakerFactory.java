package com.mageddo.dnsproxyserver.solver.remote.application.failsafe;

import com.mageddo.commons.lang.tuple.Pair;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.application.ConfigService;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import com.mageddo.dnsproxyserver.solver.remote.Result;
import com.mageddo.dnsproxyserver.solver.remote.application.FailsafeCircuitBreakerFactory;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateNonResilient;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateStaticThresholdFailsafe;
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
/**
 * fixme #533 Move failsafe.CircuitBreakerFactory  to another package, this is not specific for failsafe
 */
public class CircuitBreakerFactory {

  private final Map<InetSocketAddress, CircuitBreakerDelegate> circuitBreakerMap = new ConcurrentHashMap<>();
  private final ConfigService configService;

  // fixme #533 Delete CircuitBreakerPingCheckerService from CircuitBreakerFactory and related stuff
  private final CircuitBreakerPingCheckerService circuitBreakerCheckerService;

  private final FailsafeCircuitBreakerFactory failsafeCircuitBreakerFactory;
  private final com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold.CircuitBreakerFactory canaryThresholdFactory;

  public Result check(InetSocketAddress remoteAddress, Supplier<Result> sup) {
    final var circuitBreaker = this.findCircuitBreaker(remoteAddress);
    return circuitBreaker.execute(sup);
  }

  public CircuitBreakerDelegate findCircuitBreaker(InetSocketAddress address) {
    return this.circuitBreakerMap.computeIfAbsent(address, addr -> this.findCircuitBreakerHotLoad(address));
  }

  CircuitBreakerDelegate findCircuitBreakerHotLoad(InetSocketAddress address) {
    final var config = this.findCircuitBreakerConfig();
    return switch (config.name()) {
      case STATIC_THRESHOLD -> this.buildStaticThresholdFailSafeCircuitBreaker(address, config);
      case NON_RESILIENT -> new CircuitBreakerDelegateNonResilient();
      case CANARY_RATE_THRESHOLD -> this.buildCanaryRateThreshold(config);
      default -> throw new UnsupportedOperationException();
    };
  }

  CircuitBreakerDelegate buildCanaryRateThreshold(CircuitBreakerStrategyConfig config) {
    return this.canaryThresholdFactory.build(config);
  }

  private CircuitBreakerDelegateStaticThresholdFailsafe buildStaticThresholdFailSafeCircuitBreaker(
    InetSocketAddress address, CircuitBreakerStrategyConfig config
  ) {
    return new CircuitBreakerDelegateStaticThresholdFailsafe(this.failsafeCircuitBreakerFactory.build(
      address,
      (StaticThresholdCircuitBreakerStrategyConfig) config
    ));
  }

  CircuitBreakerStrategyConfig findCircuitBreakerConfig() {
    return this.configService.findCurrentConfigCircuitBreaker();
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

  boolean circuitBreakerSafeCheck(Map.Entry<InetSocketAddress, CircuitBreakerDelegate> entry) {
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
    final var circuitBreaker = this.findCircuitBreakerFromCache(remoteAddress);
    if (circuitBreaker == null) {
      return null;
    }
    return circuitBreaker.findStatus();
  }

  private Stats toStats(InetSocketAddress remoteAddr) {
    final var circuitBreaker = this.findCircuitBreakerFromCache(remoteAddr);
    final var state = circuitBreaker.findStatus().name();
    return Stats.of(remoteAddr.toString(), state);
  }

  private CircuitBreakerDelegate findCircuitBreakerFromCache(InetSocketAddress remoteAddress) {
    return this.circuitBreakerMap.get(remoteAddress);
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
