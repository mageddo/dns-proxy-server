package com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.canaryratethreshold;

import java.time.Duration;

import com.mageddo.commons.circuitbreaker.CircuitCheckException;
import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

public class Resilience4jMapper {
  public static CircuitBreaker from(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    final var circuitBreaker = CircuitBreaker.of(
        "defaultCircuitBreaker",
        CircuitBreakerConfig
            .custom()

            .failureRateThreshold(config.getFailureRateThreshold())
            .minimumNumberOfCalls(config.getMinimumNumberOfCalls())
            .permittedNumberOfCallsInHalfOpenState(
                config.getPermittedNumberOfCallsInHalfOpenState())

            .waitDurationInOpenState(Duration.ofDays(365))
            .recordExceptions(CircuitCheckException.class)

            .build()
    );
    return circuitBreaker;
  }
}
