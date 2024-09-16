package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFactory {

  public CircuitBreakerDelegateSelfObservable build(CanaryRateThresholdCircuitBreakerStrategyConfig config){
    final var circuitBreakerDelegate = new CircuitBreakerDelegateCanaryRateThreshold(
      this.createResilienceCircuitBreakerFrom(config)
    );
    final var healthChecker = new CircuitExecutionsAsHealthChecker(circuitBreakerDelegate);
    return new CircuitBreakerDelegateSelfObservable(
      healthChecker, healthChecker
    );
  }

  private CircuitBreaker createResilienceCircuitBreakerFrom(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    return Resilience4jMapper.from(config);
  }
}
