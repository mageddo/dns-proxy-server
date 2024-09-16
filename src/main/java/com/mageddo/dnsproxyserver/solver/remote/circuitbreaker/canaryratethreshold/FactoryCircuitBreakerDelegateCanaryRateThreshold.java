package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import javax.inject.Singleton;

@Singleton
public class FactoryCircuitBreakerDelegateCanaryRateThreshold {

  public CircuitBreakerDelegateSelfObservableCanaryRateThreshold build(CircuitBreakerStrategyConfig config){
    final var circuitBreakerDelegate = new CircuitBreakerDelegateCanaryRateThreshold(
      this.createResilienceCircuitBreakerFrom(config)
    );
    final var healthChecker = new CircuitExecutionsAsHealthChecker(circuitBreakerDelegate);
    return new CircuitBreakerDelegateSelfObservableCanaryRateThreshold(
      circuitBreakerDelegate,
      healthChecker
    );
  }

  private CircuitBreaker createResilienceCircuitBreakerFrom(CircuitBreakerStrategyConfig config) {
    throw new UnsupportedOperationException();
  }
}
