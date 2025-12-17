package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegate;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.DnsServerHealthChecker;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthChecker;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.HealthCheckerStatic;
import com.mageddo.net.IpAddr;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CircuitBreakerFactory {

  CircuitBreakerDelegateSelfObservable buildWithoutHealthCheck(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    return build(config, new HealthCheckerStatic(true));
  }

  public CircuitBreakerDelegateSelfObservable build(
      CanaryRateThresholdCircuitBreakerStrategyConfig config, HealthChecker healthChecker
  ) {
    final var canaryRateThresholdCircuitBreaker = new CircuitBreakerDelegateCanaryRateThreshold(
        this.createResilienceCircuitBreakerFrom(config), healthChecker.toString()
    );
    return new CircuitBreakerDelegateSelfObservable(
        canaryRateThresholdCircuitBreaker, healthChecker
    );
  }

  public CircuitBreakerDelegate build(CircuitBreakerStrategyConfig config, IpAddr addr) {
    Validate.isTrue(
        config.getType() == CircuitBreakerStrategyConfig.Type.CANARY_RATE_THRESHOLD,
        "Not the expected config: " + ClassUtils.getSimpleName(config)
    );
    return this.build((CanaryRateThresholdCircuitBreakerStrategyConfig) config,
        new DnsServerHealthChecker(addr)
    );
  }

  private CircuitBreaker createResilienceCircuitBreakerFrom(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    final var circuitBreaker = Resilience4jMapper.from(config);
    circuitBreaker.transitionToOpenState();
    return circuitBreaker;
  }

}
