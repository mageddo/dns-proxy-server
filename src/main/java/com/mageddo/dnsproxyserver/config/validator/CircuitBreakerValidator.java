package com.mageddo.dnsproxyserver.config.validator;

import com.mageddo.dnsproxyserver.config.CanaryRateThresholdCircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.CircuitBreakerStrategyConfig;
import com.mageddo.dnsproxyserver.config.StaticThresholdCircuitBreakerStrategyConfig;

import org.apache.commons.lang3.Validate;

import static com.mageddo.dnsproxyserver.utils.Numbers.positiveOrNull;

public class CircuitBreakerValidator {
  public static void validate(CircuitBreakerStrategyConfig config) {
    try {
      switch (config.getType()) {
        case STATIC_THRESHOLD -> validate((StaticThresholdCircuitBreakerStrategyConfig) config);
        case CANARY_RATE_THRESHOLD ->
            validate((CanaryRateThresholdCircuitBreakerStrategyConfig) config);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("status=badParams, circuitBreaker=%s: %s", config.getType(), e.getMessage()),
          e
      );
    }
  }

  static void validate(CanaryRateThresholdCircuitBreakerStrategyConfig config) {
    Validate.notNull(positiveOrNull(config.getMinimumNumberOfCalls()),
        genMsg("failure threshold must be a positive number")
    );
    Validate.notNull(positiveOrNull(config.getPermittedNumberOfCallsInHalfOpenState()),
        genMsg("success threshold must be positive number")
    );
    Validate.notNull(positiveOrNull(config.getFailureRateThreshold()),
        genMsg("success thershold capacity must be positive number")
    );
  }

  static void validate(StaticThresholdCircuitBreakerStrategyConfig config) {
    Validate.notNull(positiveOrNull(config.getFailureThreshold()),
        genMsg("failure threshold must be a positive number")
    );
    Validate.notNull(positiveOrNull(config.getSuccessThreshold()),
        genMsg("success threshold must be positive number")
    );
    Validate.notNull(positiveOrNull(config.getFailureThresholdCapacity()),
        genMsg("success thershold capacity must be positive number")
    );
    Validate.notNull(config.getTestDelay(), genMsg("test delay must be not null"));
  }

  private static String genMsg(String msg) {
    return "Circuit Breaker: " + msg;
  }
}
