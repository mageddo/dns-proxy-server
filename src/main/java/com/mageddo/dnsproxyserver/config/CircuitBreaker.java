package com.mageddo.dnsproxyserver.config;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;


@Value
@Builder
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

  public static CircuitBreaker empty() {
    return CircuitBreaker.builder().build();
  }
}
