package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CircuitBreakerFactory {

  public CircuitBreakerDelegateSelfObservable build(CanaryRateThresholdCircuitBreakerStrategyConfig config, String name) {
    final var circuitBreakerDelegate = new CircuitBreakerDelegateCanaryRateThreshold(
      this.createResilienceCircuitBreakerFrom(config), name
    );
    final var healthChecker = new CircuitExecutionsAsHealthChecker(circuitBreakerDelegate);
    return new CircuitBreakerDelegateSelfObservable(
      healthChecker, healthChecker
    );
  }

  public CircuitBreakerDelegate build(CircuitBreakerStrategyConfig config, String name) {
    Validate.isTrue(
      config.name() == CircuitBreakerStrategyConfig.Name.CANARY_RATE_THRESHOLD,
      "Not the expected config: " + ClassUtils.getSimpleName(config)
    );
    return this.build((CanaryRateThresholdCircuitBreakerStrategyConfig) config, name);
  }

  private CircuitBreaker createResilienceCircuitBreakerFrom(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    return Resilience4jMapper.from(config);
  }

}
