package com.mageddo.dnsproxyserver.config;

import java.time.Duration;


public class CircuitBreaker {

  /**
   * See {@link dev.failsafe.CircuitBreakerBuilder#withFailureThreshold(int, int)}
   */
  private Integer failureThreshold;
  private Integer failureThresholdCapacity;

  /**
   * @see dev.failsafe.CircuitBreakerBuilder#withSuccessThreshold(int)
   */
  private Integer successThreshold;

  /**
   * @see dev.failsafe.CircuitBreakerBuilder#withDelay(Duration)
   */
  private Duration testDelay;
}
