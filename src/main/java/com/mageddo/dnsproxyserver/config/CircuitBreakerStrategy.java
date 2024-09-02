package com.mageddo.dnsproxyserver.config;

import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateNonResilient;
import com.mageddo.dnsproxyserver.solver.remote.circuitbreaker.application.CircuitBreakerDelegateStaticThresholdFailsafe;

public interface CircuitBreakerStrategy {

  Name name();

  enum Name {
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
