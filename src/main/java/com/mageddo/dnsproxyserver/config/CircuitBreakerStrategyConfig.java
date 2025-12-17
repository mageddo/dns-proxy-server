package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateNonResilient;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateStaticThresholdFailsafe;

public interface CircuitBreakerStrategyConfig {

  Type getType();

  enum Type {
    /**
     * @see CircuitBreakerDelegateStaticThresholdFailsafe
     */
    STATIC_THRESHOLD,

    CANARY_RATE_THRESHOLD,

    /**
     * @see CircuitBreakerDelegateNonResilient
     */
    NON_RESILIENT,
  }
}
